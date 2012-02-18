package org.hermitcrab.util;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;

public class InterestingConfigChanges {

	final Configuration mLastConfiguration = new Configuration();
	int mLastDensity;

	public boolean applyNewConfig(Resources res) {
		int configChanges = mLastConfiguration.updateFrom(res
				.getConfiguration());
		boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
		if (densityChanged
				|| (configChanges & (ActivityInfo.CONFIG_LOCALE
						| ActivityInfo.CONFIG_UI_MODE | ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
			mLastDensity = res.getDisplayMetrics().densityDpi;
			return true;
		}
		return false;
	}

}
