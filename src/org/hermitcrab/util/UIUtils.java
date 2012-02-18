package org.hermitcrab.util;

import android.graphics.Bitmap;

public class UIUtils {

	public static void safelyRecycle(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

}
