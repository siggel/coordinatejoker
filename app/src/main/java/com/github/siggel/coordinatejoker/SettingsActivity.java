/*
 * Copyright (c) 2018 by siggel <siggel-apps@gmx.de>
 *
 *     This file is part of Coordinate Joker.
 *
 *     Coordinate Joker is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Coordinate Joker is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Coordinate Joker.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.siggel.coordinatejoker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import androidx.core.app.NavUtils;

import java.util.Objects;


@SuppressWarnings("deprecation")
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        // set default values if settings (partially) not available yet
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        initializeSummaries();
    }

    private void initializeSummaries() {
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), getString(R.string.key_use_with));
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), getString(R.string.key_format));
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        final Preference preference = findPreference(key);

        // for SwitchPreferences update switch state
        if (preference instanceof SwitchPreference) {
            final boolean booleanValue = sharedPreferences.getBoolean(key, false);
            ((SwitchPreference) preference).setChecked(booleanValue);
        }
        // for ListPreferences update summary
        else if (preference instanceof ListPreference) {

            final String stringValue = sharedPreferences.getString(key, "");

            // set summary to translated value (entry value -> entry)
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        }

        // update expert settings if "use with" changed
        if (key.equals(getString(R.string.key_use_with))) {
            final String stringValue = Objects.requireNonNull(sharedPreferences.getString(key, ""));

            if (!("expert".equals(stringValue))) {
                ExportSettings exportSettings = new ExportSettings(stringValue);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.key_share), exportSettings.isWantsToShare());
                editor.putBoolean(getString(R.string.key_use_mime), exportSettings.isUseMimeType());
                editor.putString(getString(R.string.key_format), exportSettings.getFormat());
                editor.apply();
            }

            toggleExpertSettings(stringValue);
        }
    }

    private void toggleExpertSettings(String stringValue) {
        final PreferenceCategory expertCategory = (PreferenceCategory) findPreference(getString(R.string.key_expert_category));
        expertCategory.setEnabled(stringValue.equals("expert"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
