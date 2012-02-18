/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hermitcrab.util;

import android.graphics.Bitmap;

/**
 * Static library version of {@code android.util.LruCache}. Used to write apps
 * that run on API levels prior to 12. When running on API level 12 or above,
 * this implementation is still used; it does not try to switch to the
 * framework's implementation. See the framework SDK documentation for a class
 * overview.
 */
public class LruBitmapCache<K> extends LruSoftCache<K, Bitmap> {

	private static final String LOG_TAG = LruBitmapCache.class.getSimpleName();

	public LruBitmapCache(int maxSize) {
		super(maxSize);
	}

}
