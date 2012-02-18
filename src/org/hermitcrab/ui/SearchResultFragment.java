package org.hermitcrab.ui;

import org.hermitcrab.R;
import org.hermitcrab.entity.Software;
import org.hermitcrab.ui.adapter.SoftwareAlternativeLoader;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultFragment extends GridFragment implements
		LoaderManager.LoaderCallbacks<Software[]> {

	public static final String EXTRA_SOFTWARE = "extra.software";

	private String mSoftwareId;
	private SoftwareAdapter mAdapter;

	private void die(boolean toast) {
		if (toast) {
			Toast.makeText(getActivity(), R.string.empty_applications,
					Toast.LENGTH_LONG).show();
		}
		getActivity().finish();
	}

	public void setSoftwareId(String id) {
		mSoftwareId = id;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.d("wuman", "id: " + mSoftwareId);

		if (TextUtils.isEmpty(mSoftwareId)) {
			die(true);
		}

		setEmptyText(getActivity().getString(R.string.empty_applications));

		mAdapter = new SoftwareAdapter(getActivity());
		setGridAdapter(mAdapter);

		setGridShown(false);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getGridView().setNumColumns(3);
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
			super(context, R.layout.app_avatar);
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
				view = mInflater.inflate(R.layout.app_avatar, parent, false);
			} else {
				view = convertView;
			}

			Software software = getItem(position);
			TextView tv;
			tv = (TextView) view.findViewById(R.id.text_app_name);
			tv.setText(software.name);
			tv = (TextView) view.findViewById(R.id.text_like_number);
			tv.setText(Integer.toString(software.votes));
			ImageView image = (ImageView) view
					.findViewById(R.id.image_app_icon);
			image.setImageResource(R.drawable.ic_launcher);

			return view;
		}
	}

	@Override
	public Loader<Software[]> onCreateLoader(int id, Bundle args) {
		return new SoftwareAlternativeLoader(getActivity(), mSoftwareId);
	}

	@Override
	public void onLoadFinished(Loader<Software[]> loader, Software[] data) {
		mAdapter.setData(data);

		if (isResumed()) {
			setGridShown(true);
		} else {
			setGridShownNoAnimation(true);
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
