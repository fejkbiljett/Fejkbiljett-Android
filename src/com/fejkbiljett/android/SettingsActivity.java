package com.fejkbiljett.android;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		Preference button = (Preference)findPreference("sms_permission");
		
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) { 		    
					Intent intent = new Intent();
					intent.setClassName("com.android.settings", "com.android.settings.Settings");
					intent.setAction(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					intent.putExtra(":android:show_fragment", "com.android.settings.applications.AppOpsSummary");
					startActivity(intent);				
					return true;
			    }
			});
		} else {
			button.setShouldDisableView(true);
			button.setEnabled(false);
		}
	}
}
