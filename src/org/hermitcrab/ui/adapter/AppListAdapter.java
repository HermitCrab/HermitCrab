package org.hermitcrab.ui.adapter;

import java.util.List;

import org.hermitcrab.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AppListAdapter extends ArrayAdapter<AppEntry> {

	private final LayoutInflater mInflater;
	private final Resources mRes;

	public AppListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRes = context.getResources();
	}

	public void setData(List<AppEntry> data) {
		clear();
		if (data != null) {
			for (AppEntry entry : data) {
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

		AppEntry item = getItem(position);
		TextView tv = (TextView) view.findViewById(android.R.id.text1);

		Drawable icon = item.getIcon(mInflater.getContext());
		final int iconSize = mRes.getDimensionPixelSize(R.dimen.app_icon_size);
		final int iconPadding = mRes
				.getDimensionPixelSize(R.dimen.icon_padding);
		icon.setBounds(0, 0, iconSize, iconSize);

		tv.setCompoundDrawables(icon, null, null, null);
		tv.setCompoundDrawablePadding(iconPadding);
		tv.setText(item.getLabel());

		return view;
	}
}
