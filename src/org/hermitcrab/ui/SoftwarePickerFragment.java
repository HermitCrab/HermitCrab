package org.hermitcrab.ui;

import org.hermitcrab.R;
import org.hermitcrab.entity.Software;
import org.hermitcrab.ui.adapter.AppEntry;
import org.hermitcrab.ui.adapter.SoftwarePickerLoader;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SoftwarePickerFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Software[]> {

	public static final String EXTRA_APP_ENTRY = "extra.app_entry";

	private AppEntry mApp;
	private SoftwareAdapter mAdapter;

	private void die(boolean toast) {
		if (toast) {
			Toast.makeText(getActivity(), R.string.empty_applications,
					Toast.LENGTH_LONG).show();
		}
		getActivity().finish();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		mApp = (AppEntry) (intent == null ? null : intent
				.getParcelableExtra(EXTRA_APP_ENTRY));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mApp == null) {
			die(true);
		}

		setEmptyText(getActivity().getString(R.string.empty_applications));

		mAdapter = new SoftwareAdapter(getActivity());
		setListAdapter(mAdapter);

		setListShown(false);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onPause() {
		super.onPause();
		die(false);
	}

	@Override
	public void onDestroy() {
		getLoaderManager().destroyLoader(0);
		super.onDestroy();
	}

	private static final class SoftwareAdapter extends ArrayAdapter<Software> {

		private final LayoutInflater mInflater;
		private final Resources mRes;

		public SoftwareAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_2);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mRes = context.getResources();
		}

		public void setData(Software[] data) {
			clear();
			if (data != null) {
				for (Software entry : data) {
					add(entry);
				}
			}
		}

		/**
		 * Populate new items in the list.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				view = mInflater.inflate(android.R.layout.simple_list_item_1,
						parent, false);
			} else {
				view = convertView;
			}

			Software software = getItem(position);
			TextView tv = (TextView) view.findViewById(android.R.id.text1);

			tv.setText(software.name);

			return view;
		}
	}

	@Override
	public Loader<Software[]> onCreateLoader(int id, Bundle args) {
		return new SoftwarePickerLoader(getActivity(), mApp);
	}

	@Override
	public void onLoadFinished(Loader<Software[]> loader, Software[] data) {
		mAdapter.setData(data);

		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}

		if (data == null || data.length == 0) {
			die(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Software[]> loader) {
		mAdapter.setData(null);
	}

}
