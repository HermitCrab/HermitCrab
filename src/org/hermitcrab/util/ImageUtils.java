package org.hermitcrab.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

public class ImageUtils {

	private static final String LOG_TAG = ImageUtils.class.getSimpleName();

	private static final int STRATEGY_POWER_OF_TWO = 0;
	private static final int STRATEGY_EXACT_RATIO = 1;
	private static final int STRATEGY = STRATEGY_POWER_OF_TWO;

	public static int getSampleSize(int origWidth, int origHeight,
			int viewWidth, int viewHeight) {
		int maxSize = Math.max(viewWidth, viewHeight);
		int largest = Math.max(origWidth, origHeight);
		int sample = 1;
		if (STRATEGY == STRATEGY_POWER_OF_TWO) {
			while (largest > maxSize) {
				sample *= 2;
				largest /= 2;
			}
		} else {
			sample = largest / maxSize;
		}
		return Math.max(1, sample);
	}

	public static Bitmap resizeBitmap(Context context, String url,
			int viewWidth, int viewHeight) throws IOException {
		URL imageUrl = new URL(url);
		InputStream inputStream = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = null;

		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			inputStream = new BufferedInputStream(conn.getInputStream());
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(inputStream, null, options);
		} finally {
			FileUtils.closeSilently(inputStream);
		}

		final int oW = options.outWidth;
		final int oH = options.outHeight;
		options.inJustDecodeBounds = false;
		options.inScaled = true;
		options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
		options.inTargetDensity = context.getResources().getDisplayMetrics().densityDpi;
		options.inSampleSize = ImageUtils.getSampleSize(oW, oH, viewWidth,
				viewHeight);
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			inputStream = new BufferedInputStream(conn.getInputStream());
			bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		} finally {
			FileUtils.closeSilently(inputStream);
		}

		Log.d(LOG_TAG, "Fetching: inSampleSize: " + options.inSampleSize
				+ ", (" + viewWidth + ", " + viewHeight + "): " + "(" + oW
				+ ", " + oH + ") => (" + options.outWidth + ", "
				+ options.outHeight + "); " + url + ", bitmap: " + bitmap);

		return bitmap;
	}

	public static Bitmap writeImageToDiskCache(Context context,
			String filename, Bitmap bitmap) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			Log.i(LOG_TAG, "writeImageToDiskCache: " + filename
					+ " to disk cache");
		} catch (FileNotFoundException e) {
			Log.w(LOG_TAG, "Unable to openCacheFileOutput", e);
		} finally {
			FileUtils.closeSilently(fos);
		}
		return bitmap;
	}

	public static Bitmap readImageFromDiskCache(Context context, String filename) {
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(filename);
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
			if (bitmap != null) {
				Log.i(LOG_TAG, "readImageFromDiskCache: " + filename);
			} else {
				Log.w(LOG_TAG,
						"Cached image exists on disk but cannot be decoded: "
								+ filename);
			}
			return bitmap;
		} catch (FileNotFoundException e) {
			Log.w(LOG_TAG, "Cached image does not exist on disk: " + filename);
		} finally {
			FileUtils.closeSilently(fis);
		}
		return null;
	}
}
