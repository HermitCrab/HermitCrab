package org.hermitcrab.ui;

import java.util.List;

import org.hermitcrab.R;
import org.hermitcrab.ui.adapter.AppEntry;
import org.hermitcrab.ui.adapter.AppListAdapter;
import org.hermitcrab.ui.adapter.RecentAppListLoader;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class AppListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<AppEntry>> {

	private static final String LOG_TAG = AppListFragment.class.getSimpleName();

	// This is the Adapter being used to display the list's data.
	AppListAdapter mAdapter;

	// If non-null, this is the current filter the user has provided.
	String mCurFilter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Give some text to display if there is no data. In a real
		// application this would come from a resource.
		setEmptyText(getActivity().getString(R.string.empty_applications));

		// Create an empty adapter we will use to display the loaded data.
		mAdapter = new AppListAdapter(getActivity());
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Insert desired behavior here.
		Log.i("LoaderCustom", "Item clicked: " + id);
	}

	@Override
	public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader with no arguments, so it is simple.
		return new RecentAppListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<AppEntry>> loader,
			List<AppEntry> data) {
		// Set the new data in the adapter.
		mAdapter.setData(data);

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
		mAdapter.setData(null);
	}

}
