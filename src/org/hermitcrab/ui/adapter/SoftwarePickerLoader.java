package org.hermitcrab.ui.adapter;

import java.util.HashSet;

import org.hermitcrab.api.AlternativeTo;
import org.hermitcrab.entity.Software;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

public class SoftwarePickerLoader extends AsyncTaskLoader<Software[]> {

	private final AppEntry mAppEntry;

	Software[] mApps;

	public SoftwarePickerLoader(Context context, AppEntry appEntry) {
		super(context);
		mAppEntry = appEntry;
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public Software[] loadInBackground() {
		HashSet<String> keywords = new HashSet<String>();

		String packageName = mAppEntry.getApplicationInfo().packageName;
		String[] segments = TextUtils.split(packageName, ".");
		for (String segment : segments) {
			if (segment.length() > 3) {
				keywords.add(segment);
			}
		}
		segments = TextUtils.split(mAppEntry.getLabel(), " ");
		for (String segment : segments) {
			if (segment.length() > 3) {
				keywords.add(segment);
			}
		}

		Software[] softwares = AlternativeTo.search(
				TextUtils.join("%20", keywords),
				new String[] { AlternativeTo.PLATFORM_ANDROID });

		return softwares;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(Software[] apps) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (apps != null) {
				onReleaseResources(apps);
			}
		}
		Software[] oldApps = apps;
		mApps = apps;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(apps);
		}

		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldApps != null) {
			onReleaseResources(oldApps);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (mApps != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mApps);
		}

		if (takeContentChanged() || mApps == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(Software[] apps) {
		super.onCanceled(apps);

		// At this point we can release the resources associated with 'apps'
		// if needed.
		onReleaseResources(apps);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (mApps != null) {
			onReleaseResources(mApps);
			mApps = null;
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	protected void onReleaseResources(Software[] apps) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}

}
