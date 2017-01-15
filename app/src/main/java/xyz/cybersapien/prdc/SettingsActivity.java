package xyz.cybersapien.prdc;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by ogcybersapien on 1/6/2017.
 * Settings Activity for selecting display Units
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SettingsTheme);
        super.onCreate(savedInstanceState);

        // Display Fragment as main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Add the preference screen
            addPreferencesFromResource(R.xml.preferences);

            // Set up the units preferences
            Preference units = findPreference(getString(R.string.prefs_units_key));
            units.setOnPreferenceChangeListener(this);
            onPreferenceChange(units, PreferenceManager
                    .getDefaultSharedPreferences(units.getContext()).getString(units.getKey(), ""));
            bindPreferenceSummaryToValue(units);
        }

        private void bindPreferenceSummaryToValue(Preference preference){
            // Set listener
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String strValue = newValue.toString();
            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(strValue);
                if (prefIndex >= 0){
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                preference.setSummary(strValue);
            }
            return true;
        }
    }
}
