package org.hermitcrab.util;

import java.util.ArrayList;

import org.hermitcrab.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class PhotoCache extends UrlImagesCache {

	private static final String LOG_TAG = PhotoCache.class.getSimpleName();

	private static final int CACHE_SIZE = 30;
	private static final int CORE_THREAD_POOL_SIZE = 5;
	private static final int MAX_THREAD_POOL_SIZE = 5;
	private static final int TASK_QUEUE_SIZE = 10;
	private static final String THREAD_NAME_PREFIX = "PhotoTask";

	private final ArrayList<PhotoCacheListener> mListeners;

	public PhotoCache() {
		super(CACHE_SIZE, CORE_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE,
				TASK_QUEUE_SIZE, THREAD_NAME_PREFIX);
		mListeners = new ArrayList<PhotoCacheListener>();
	}

	@Override
	protected void ensureExecutorService() {
		PhotoImageTask.setDefaultExecutor(mExecutor);
	}

	@Override
	protected Bitmap onCacheMiss(Context context, String url) {
		new PhotoImageTask(context).execute(url);
		return null;
	}

	@Override
	protected Bitmap loadFromCloud(Context context, String url) {
		// TODO: Might not want to scale it here. Might want to scale this in
		// the ImageView.
		final int wh = context.getResources().getDimensionPixelSize(
				R.dimen.photo_thumbnail_size);

		try {
			return ImageUtils.resizeBitmap(context, url, wh, wh);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to loadFromCloud: " + url, e);
		}

		return null;
	}

	@Override
	protected Bitmap fromPersistence(Context context, String url) {
		try {
			return ImageUtils.readImageFromDiskCache(context, FileUtils
					.makeUUID(url).toString());
		} catch (Exception e) {
			Log.w(LOG_TAG, "Unable to decode Bitmap from persistence: " + url,
					e);
		}
		return null;
	}

	@Override
	protected void toPersistence(Context context, String url, Bitmap bitmap) {
		try {
			ImageUtils.writeImageToDiskCache(context, FileUtils.makeUUID(url)
					.toString(), bitmap);
		} catch (Exception e) {
			Log.w(LOG_TAG, "Failed to write to persistence: " + url, e);
		}
	}

	@Override
	protected void onCacheMissComplete(String... urls) {
		for (PhotoCacheListener listener : mListeners) {
			listener.onPhotosLoaded(this, urls);
		}
	}

	public static interface PhotoCacheListener {
		public void onPhotosLoaded(PhotoCache cache, String... urls);
	}

	public void addListener(PhotoCacheListener listener) {
		mListeners.add(listener);
	}

	public void removeListener(PhotoCacheListener listener) {
		mListeners.remove(listener);
	}

	public final class PhotoImageTask extends BaseUrlImageTask {

		public PhotoImageTask(Context context) {
			super(context);
			ensureExecutorService();
		}

	}

}
