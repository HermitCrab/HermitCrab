package org.hermitcrab.ui.adapter;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppEntry implements Parcelable {

	public static final Parcelable.Creator<AppEntry> CREATOR = new Parcelable.Creator<AppEntry>() {

		@Override
		public AppEntry createFromParcel(Parcel source) {
			return new AppEntry(source);
		}

		@Override
		public AppEntry[] newArray(int size) {
			return new AppEntry[size];
		}
	};

	private final ApplicationInfo mInfo;
	private final File mApkFile;
	private String mLabel;
	private Drawable mIcon;
	private boolean mMounted;

	public AppEntry(ApplicationInfo info) {
		mInfo = info;
		mApkFile = new File(info.sourceDir);
	}

	public AppEntry(Parcel in) {
		mLabel = in.readString();
		mMounted = (in.readInt() == 1);
		mInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
		mApkFile = new File(mInfo.sourceDir);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mLabel);
		out.writeInt(mMounted ? 1 : 0);
		out.writeParcelable(mInfo, flags);
	}

	public ApplicationInfo getApplicationInfo() {
		return mInfo;
	}

	public String getLabel() {
		return mLabel;
	}

	public Drawable getIcon(Context context) {
		if (mIcon == null) {
			if (mApkFile.exists()) {
				mIcon = mInfo.loadIcon(context.getPackageManager());
				return mIcon;
			} else {
				mMounted = false;
			}
		} else if (!mMounted) {
			// If the app wasn't mounted but is now mounted, reload
			// its icon.
			if (mApkFile.exists()) {
				mMounted = true;
				mIcon = mInfo.loadIcon(context.getPackageManager());
				return mIcon;
			}
		} else {
			return mIcon;
		}

		return context.getResources().getDrawable(
				android.R.drawable.sym_def_app_icon);
	}

	@Override
	public String toString() {
		return mLabel;
	}

	void loadLabel(Context context) {
		if (mLabel == null || !mMounted) {
			if (!mApkFile.exists()) {
				mMounted = false;
				mLabel = mInfo.packageName;
			} else {
				mMounted = true;
				CharSequence label = mInfo.loadLabel(context
						.getPackageManager());
				mLabel = label != null ? label.toString() : mInfo.packageName;
			}
		}
	}

	/**
	 * Perform alphabetical comparison of application entry objects.
	 */
	public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
		private final Collator sCollator = Collator.getInstance();

		@Override
		public int compare(AppEntry object1, AppEntry object2) {
			return sCollator.compare(object1.getLabel(), object2.getLabel());
		}
	};

}