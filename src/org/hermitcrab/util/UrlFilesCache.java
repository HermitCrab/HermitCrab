package org.hermitcrab.util;

import java.util.HashSet;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.hermitcrab.util.concurrent.LinkedBlockingLifoQueue;

import android.content.Context;
import android.util.Log;

public abstract class UrlFilesCache<V> {

	private final int mCacheSize;
	private final int mCoreThreadPoolSize;
	private final int mMaxThreadPoolSize;
	private final String mThreadNamePrefix;
	private final int mTaskQueueSize;
	private LruSoftCache<String, V> mCache;
	private final HashSet<String> mPendingDownloads;

	protected final ThreadPoolExecutor mExecutor;

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	public UrlFilesCache(int cacheSize, int coreThreadPoolSize,
			int maxThreadPoolSize, int taskQueueSize, String threadNamePrefix) {
		super();
		mCacheSize = cacheSize;
		mCoreThreadPoolSize = coreThreadPoolSize;
		mMaxThreadPoolSize = maxThreadPoolSize;
		mTaskQueueSize = taskQueueSize;
		mThreadNamePrefix = threadNamePrefix;
		mExecutor = new ThreadPoolExecutor(mCoreThreadPoolSize,
				mMaxThreadPoolSize, BaseUrlImageTask.KEEP_ALIVE,
				TimeUnit.SECONDS, new LinkedBlockingLifoQueue<Runnable>(
						mTaskQueueSize), new ThreadFactory() {

					private final AtomicInteger mCount = new AtomicInteger(1);

					public Thread newThread(Runnable r) {
						return new Thread(r, mThreadNamePrefix + " #"
								+ mCount.getAndIncrement());
					}

				}, new ThreadPoolExecutor.DiscardOldestPolicy());
		mPendingDownloads = new HashSet<String>();
	}

	private void ensureCache() {
		if (mCache == null) {
			mCache = new LruSoftCache<String, V>(mCacheSize) {

				@Override
				protected V create(Context context, String key) {
					return onCacheMiss(context, key);
				}

			};
			ensureExecutorService();
		}
	}

	protected abstract void ensureExecutorService();

	/**
	 * Cache miss will always return a null Bitmap immediately.
	 * 
	 * It will also trigger a persistence load if one is available.
	 * 
	 * If the persistence load is also a miss, then a remote fetch will be
	 * triggered.
	 * 
	 * @param url
	 * @return
	 */
	protected abstract V onCacheMiss(Context context, String url);

	public final V get(Context context, String url) {
		ensureCache();
		return mCache.get(context, url);
	}

	protected V fromPersistence(Context context, String url) {
		return null;
	}

	protected void toPersistence(Context context, String url, V object) {
	}

	protected abstract V loadFromCloud(Context context, String url);

	/**
	 * This can also be interpreted as onImageLoaded(String... urls)
	 * 
	 * @param urls
	 */
	protected abstract void onCacheMissComplete(String... urls);

	public class BaseUrlImageTask extends
			CompatAsyncTask<String, Object, String[]> {

		private static final int KEEP_ALIVE = 1;

		protected final Context mContext;

		public BaseUrlImageTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected String[] doInBackground(String... params) {
			HashSet<String> urlsHandled = new HashSet<String>();

			if (params != null && params.length > 0) {
				Log.d(Thread.currentThread().getName(), "doInBackground: "
						+ params[0]);
			}

			for (String url : params) {
				// Breakpoint 1
				if (isCancelled()) {
					break;
				}

				V object = fromPersistence(mContext, url);

				// Breakpoint 2
				if (isCancelled()) {
					break;
				}

				if (object != null) {
					urlsHandled.add(url);
					publishProgress(url, object);
					continue;
				}

				// Breakpoint 3
				if (isCancelled()) {
					break;
				}

				boolean isPending;
				synchronized (mPendingDownloads) {
					isPending = mPendingDownloads.contains(url);
				}
				if (!isPending) {
					synchronized (mPendingDownloads) {
						mPendingDownloads.add(url);
					}
					object = loadFromCloud(mContext, url);
					synchronized (mPendingDownloads) {
						mPendingDownloads.remove(url);
					}
					if (object != null) {
						toPersistence(mContext, url, object);
						urlsHandled.add(url);
						publishProgress(url, object);
						continue;
					}
				}
			}

			return urlsHandled.toArray(EMPTY_STRING_ARRAY);
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			String url = (String) values[0];
			V object = null;
			try {
				object = (V) values[1];
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), "Failed to cast", e);
			}
			if (object != null) {
				ensureCache();
				mCache.put(url, object);
				return;
			}
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (result != null && result.length > 0) {
				onCacheMissComplete(result);
			}
		}

	}

}
