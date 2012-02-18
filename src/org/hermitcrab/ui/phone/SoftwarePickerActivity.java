package org.hermitcrab.ui.phone;

import org.hermitcrab.ui.BaseSinglePaneActivity;
import org.hermitcrab.ui.SoftwarePickerFragment;

import android.support.v4.app.Fragment;

public class SoftwarePickerActivity extends BaseSinglePaneActivity {

	@Override
	protected Fragment onCreatePane() {
		return new SoftwarePickerFragment();
	}

}
