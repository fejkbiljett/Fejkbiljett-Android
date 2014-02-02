package com.fejkbiljett.android;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		Preference permissionButton = (Preference)findPreference("sms_permission");
		Preference checkVersionButton = (Preference)findPreference("check_version");
		checkVersionButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 		    
				new CheckVersion().execute(getApplicationContext());
				return true;
		    }
		});

		if (android.os.Build.VERSION.SDK_INT >= 19) {
			permissionButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) { 		    
					Intent intent = Utils.getAppOpsIntent();
					startActivity(intent);				
					return true;
			    }
			});
		} else {
			permissionButton.setShouldDisableView(true);
			permissionButton.setEnabled(false);
		}
	}
}
