package org.hermitcrab.ui.phone;

import org.hermitcrab.R;
import org.hermitcrab.ui.AppDetailFragment;
import org.hermitcrab.ui.BaseMultiPaneActivity;
import org.hermitcrab.ui.SearchResultFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class SearchResultActivity extends BaseMultiPaneActivity implements
		FragmentManager.OnBackStackChangedListener {

	private FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_result_page);

		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.addOnBackStackChangedListener(this);
	}

	@Override
	public FragmentReplaceInfo onSubstituteFragmentForActivityLaunch(
			String activityClassName) {
		return null;
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
