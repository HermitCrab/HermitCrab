package org.hermitcrab.ui.phone;

import org.hermitcrab.R;
import org.hermitcrab.ui.AppDetailFragment;
import org.hermitcrab.ui.BaseMultiPaneActivity;
import org.hermitcrab.ui.SearchResultFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class SearchResultActivity extends BaseMultiPaneActivity implements
		FragmentManager.OnBackStackChangedListener {

	private FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_result_page);

		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.addOnBackStackChangedListener(this);

		Intent intent = getIntent();
		Log.d("wuman", "intent: " + intent + ", extras: " + intent.getExtras());

		SearchResultFragment srf = (SearchResultFragment) mFragmentManager
				.findFragmentById(R.id.fragment_search_result);
		srf.setSoftwareId(intent
				.getStringExtra(SearchResultFragment.EXTRA_SOFTWARE));

	}

	@Override
	protected void onBeforeCommitReplaceFragment(FragmentManager fm,
			FragmentTransaction ft, Fragment fragment) {
		super.onBeforeCommitReplaceFragment(fm, ft, fragment);
		if (fragment instanceof AppDetailFragment) {
			ft.addToBackStack(null);
		} else if (fragment instanceof SearchResultFragment) {
			fm.popBackStack();
		}
	}

	@Override
	public void onBackStackChanged() {
		// TODO Auto-generated method stub

	}

}
