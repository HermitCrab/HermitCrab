package org.hermitcrab.widget;

import org.hermitcrab.R;
import org.hermitcrab.util.UIUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ReflectedView extends LinearLayout {

	private static final String LOG_TAG = ReflectedView.class.getSimpleName();

	private final int mReflectionGap;
	private final int mReflectionHeight;

	private boolean mHasDrawn = false;
	private Bitmap mReflectedBitmap;

	public ReflectedView(Context context, AttributeSet attrs) {
		super(context, attrs);

		Resources res = getResources();
		mReflectionGap = res
				.getDimensionPixelSize(R.dimen.gallery_reflection_gap);
		mReflectionHeight = res
				.getDimensionPixelSize(R.dimen.gallery_reflection_height);
	}

	public ReflectedView(Context context) {
		this(context, null);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		try {
			if (!mHasDrawn) {
				mHasDrawn = true;
				if (mReflectedBitmap == null) {
					Bitmap childBmp = Bitmap.createBitmap(child.getWidth(),
							child.getHeight(), Config.ARGB_8888);
					Canvas c = new Canvas(childBmp);
					drawChild(c, child, System.currentTimeMillis());
					mReflectedBitmap = makeReflection(childBmp,
							mReflectionHeight);
				}
			}
			if (mReflectedBitmap != null) {
				canvas.drawBitmap(mReflectedBitmap, 0, child.getHeight()
						+ mReflectionGap, null);
			}
		} catch (Exception e) {
			Log.w(LOG_TAG, "drawChild failed to draw reflection!", e);
		}
		return super.drawChild(canvas, child, drawingTime);
	}

	private Bitmap makeReflection(Bitmap original, int h) {
		// Create reflection
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflection = Bitmap
				.createBitmap(original, 0, original.getHeight() - h,
						original.getWidth(), h, matrix, false);

		// Apply alpha gradient
		final int width = reflection.getWidth();
		final int height = reflection.getHeight();
		Rect rect = new Rect(0, 0, width, height);
		Bitmap maskedReflection = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Canvas c = new Canvas(maskedReflection);
		Paint p = new Paint();
		p.setAntiAlias(true);
		c.drawBitmap(reflection, rect, rect, p);
		LinearGradient shader = new LinearGradient(0, 0, 0, height, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		p.setShader(shader);
		p.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		c.drawRect(rect, p);

		// Recycle bitmaps
		UIUtils.safelyRecycle(reflection);
		UIUtils.safelyRecycle(original);
		return maskedReflection;
	}

}
