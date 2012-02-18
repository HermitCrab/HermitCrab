package org.hermitcrab.ui;

import java.util.List;

import org.hermitcrab.R;
import org.hermitcrab.ui.adapter.AppEntry;
import org.hermitcrab.ui.adapter.AppListAdapter;
import org.hermitcrab.ui.adapter.AppListLoader;
import org.hermitcrab.ui.adapter.RecentAppListLoader;
import org.hermitcrab.ui.adapter.SeparatedListAdapter;
import org.hermitcrab.ui.phone.SoftwarePickerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

public class AppListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<AppEntry>> {

	private static final String LOG_TAG = AppListFragment.class.getSimpleName();

	private static final int LOADER_RECENT_APPS = 0;
	private static final int LOADER_DOWNLOADED_APPS = 1;

	// This is the Adapter being used to display the list's data.
	AppListAdapter mRecentAdapter;
	AppListAdapter mDownloadedAdapter;
	SeparatedListAdapter mAdapter;

	// If non-null, this is the current filter the user has provided.
	String mCurFilter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Give some text to display if there is no data. In a real
		// application this would come from a resource.
		setEmptyText(getActivity().getString(R.string.empty_applications));

		// Create an empty adapter we will use to display the loaded data.
		mRecentAdapter = new AppListAdapter(getActivity());
		mDownloadedAdapter = new AppListAdapter(getActivity());
		mAdapter = new SeparatedListAdapter(getActivity());
		mAdapter.addSection(getResources().getString(R.string.recent_apps),
				mRecentAdapter);
		mAdapter.addSection(getResources().getString(R.string.downloaded),
				mDownloadedAdapter);
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(LOADER_RECENT_APPS, null, this);
		getLoaderManager().initLoader(LOADER_DOWNLOADED_APPS, null, this);
	}

	@Override
	public void onDestroy() {
		getLoaderManager().destroyLoader(LOADER_RECENT_APPS);
		getLoaderManager().destroyLoader(LOADER_DOWNLOADED_APPS);
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Object item = l.getItemAtPosition(position);
		if (item == null) {
			return;
		}
		if (item instanceof AppEntry) {
			Intent intent = new Intent(getActivity(),
					SoftwarePickerActivity.class);
			intent.putExtra(SoftwarePickerFragment.EXTRA_APP_ENTRY,
					(AppEntry) item);
			((BaseActivity) getActivity()).openActivityOrFragment(intent);
		}
	}

	@Override
	public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader with no arguments, so it is simple.
		switch (id) {
		case LOADER_RECENT_APPS:
			return new RecentAppListLoader(getActivity());
		case LOADER_DOWNLOADED_APPS:
			return new AppListLoader(getActivity());
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<List<AppEntry>> loader,
			List<AppEntry> data) {
		// Set the new data in the adapter.
		switch (loader.getId()) {
		case LOADER_RECENT_APPS:
			mRecentAdapter.setData(data);
			break;
		case LOADER_DOWNLOADED_APPS:
			mDownloadedAdapter.setData(data);
			break;
		default:
			break;
		}

		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<AppEntry>> loader) {
		// Clear the data in the adapter.
		switch (loader.getId()) {
		case LOADER_RECENT_APPS:
			mRecentAdapter.setData(null);
			break;
		case LOADER_DOWNLOADED_APPS:
			mDownloadedAdapter.setData(null);
			break;
		default:
			break;
		}
	}

}
