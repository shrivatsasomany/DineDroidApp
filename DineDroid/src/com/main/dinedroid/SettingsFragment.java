package com.main.dinedroid;
import com.main.dinedroid.SettingsFragment;
import com.main.dinedroid.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}
}
