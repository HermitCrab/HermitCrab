package org.hermitcrab.util;

import android.graphics.Bitmap;

public abstract class UrlImagesCache extends UrlFilesCache<Bitmap> {

	public UrlImagesCache(int cacheSize, int coreThreadPoolSize,
			int maxThreadPoolSize, int taskQueueSize, String threadNamePrefix) {
		super(cacheSize, coreThreadPoolSize, maxThreadPoolSize, taskQueueSize,
				threadNamePrefix);
	}

}
