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
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import locus.api.android.utils.LocusUtils;

/**
 * Activity showing help page
 */
public class IntroActivity extends AppCompatActivity {

    private static int pageNumber;
    private static boolean haveLocus;

    /**
     * Android onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        pageNumber = 0;

        haveLocus = LocusUtils.getActiveVersion(this) != null;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        TextView textView = findViewById(R.id.introTextView);

        if (haveLocus) {
            editor.putBoolean(getString(R.string.save_export_kml), true);
            editor.putBoolean(getString(R.string.save_export_with_symbol), true);
            editor.putBoolean(getString(R.string.save_export_for_view), true);
            textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_have_locus)));
        } else {
            editor.putBoolean(getString(R.string.save_export_kml), false);
            editor.putBoolean(getString(R.string.save_export_with_symbol), false);
            editor.putBoolean(getString(R.string.save_export_for_view), false);
            textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_no_locus)));
        }
        editor.apply();
    }

    /**
     * method called when user clicked the skip button
     *
     * @param view view just syntactically needed here
     */
    public void skipIntro(@SuppressWarnings("unused") View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * method called when user clicked the next button
     *
     * @param view view just syntactically needed here
     */
    public void next(@SuppressWarnings("unused") View view) {
        pageNumber++;

        TextView textView = findViewById(R.id.introTextView);
        ImageView imageView = findViewById(R.id.introImageView);
        Button nextButton = findViewById(R.id.introNextButton);
        Button skipButton = findViewById(R.id.introSkipButton);

        if (haveLocus) {
            switch (pageNumber) {
                case 1:
                    textView.setText(Html.fromHtml("On the main screen simply enter the coordinate formulas with x being the unknown variable. Select which x values shall be tried, then hit the 'Show'-button for displaying the points."));
                    setExampleFormulas();
                    break;
                case 2:
                    textView.setText(Html.fromHtml("If you have more apps than Locus which are able to receive kmz on the fly, Android may ask you to choose which app you would like to use."));
                    break;
                case 3:
                    textView.setText(Html.fromHtml("Locus will ask you where to import the waypoints. We suggest creating an own folder like 'joker'. Thus you can easily hide or clear all points at once instead of doing it for each point individually."));
                    break;
                case 4:
                    textView.setText(Html.fromHtml("Locus will show the points on the map, so you may determine, which are worth visiting.\n\nGood luck!"));
                    nextButton.setText("Ok");
                    skipButton.setVisibility(View.INVISIBLE);
                    break;
                default:
                    NavUtils.navigateUpFromSameTask(this);
            }
        }

    }

    /**
     * method called when user selected an item from the options menu
     *
     * @param item as defined by android
     * @return boolean as defined by android
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setExampleFormulas() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.save_is_north), true);
        editor.putString(getString(R.string.save_degrees_north), "53");
        editor.putString(getString(R.string.save_minutes_north), "12.3x5");
        editor.putBoolean(getString(R.string.save_is_east), true);
        editor.putString(getString(R.string.save_degrees_east), "10");
        editor.putString(getString(R.string.save_minutes_east), "12.345");
        editor.putString(getString(R.string.save_distance), "30*x");
        editor.putBoolean(getString(R.string.save_is_feet), false);
        editor.putString(getString(R.string.save_azimuth), "20*x");
        editor.putString(getString(R.string.save_x_from), "0");
        editor.putString(getString(R.string.save_x_to), "9");
        editor.apply();
    }

}
