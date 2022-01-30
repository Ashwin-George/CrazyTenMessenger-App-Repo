package com.example.android.crazytenmessenger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
    }
    public static class MyProfilePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.my_profile_page);

            Preference username=findPreference(getString(R.string.my_profile_username_key));
            bindPreferenceToSummaryValue(username);

            Preference about=findPreference(getString(R.string.my_profile_about_key));
            bindPreferenceToSummaryValue(about);
        }

        private void bindPreferenceToSummaryValue(Preference preference) {

            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String prefrenceString=sharedPreferences.getString(preference.getKey(),"");
            onPreferenceChange(preference,prefrenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String strValue=newValue.toString();
            preference.setSummary(strValue);
            return true;
        }
    }
}