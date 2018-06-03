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

    private static int pageNumber = 0;
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

        haveLocus = LocusUtils.getActiveVersion(this) != null;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (haveLocus) {
            editor.putBoolean(getString(R.string.save_export_kml), true);
            editor.putBoolean(getString(R.string.save_export_with_symbol), true);
            editor.putBoolean(getString(R.string.save_export_for_view), true);
        } else {
            editor.putBoolean(getString(R.string.save_export_kml), false);
            editor.putBoolean(getString(R.string.save_export_with_symbol), false);
            editor.putBoolean(getString(R.string.save_export_for_view), false);
        }
        editor.apply();
        adjustPageContent();
    }

    /**
     * method called when user clicked the skip button
     *
     * @param view view just syntactically needed here
     */
    public void skipIntro(@SuppressWarnings("unused") View view) {
        pageNumber = 0;
        NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * method called when user clicked the next button
     *
     * @param view view just syntactically needed here
     */
    public void next(@SuppressWarnings("unused") View view) {
        int increment = Integer.valueOf(view.getTag().toString());
        pageNumber += increment;
        adjustPageContent();
    }

    private void adjustPageContent() {
        TextView textView = findViewById(R.id.introTextView);
        ImageView imageView = findViewById(R.id.introImageView);
        Button nextButton = findViewById(R.id.introNextButton);
        Button previousButton = findViewById(R.id.introPreviousButton);
        Button skipButton = findViewById(R.id.introSkipButton);

        if (haveLocus) {
            switch (pageNumber) {
                case 0:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_have_locus)));
                    imageView.setImageResource(android.R.color.transparent);
                    previousButton.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_enter_formulas_and_show)));
                    imageView.setImageResource(R.drawable.enter_formulas_and_view);
                    previousButton.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_select_app_for_show_kmz)));
                    imageView.setImageResource(android.R.color.transparent);
                    break;
                case 3:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_locus_import)));
                    imageView.setImageResource(R.drawable.import_to_locus);
                    nextButton.setText(R.string.string_intro_button_next);
                    skipButton.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_locus_shows_points)));
                    imageView.setImageResource(R.drawable.view_in_locus);
                    nextButton.setText(R.string.string_intro_button_ok);
                    skipButton.setVisibility(View.INVISIBLE);
                    break;
                default:
                    pageNumber = 0;
                    NavUtils.navigateUpFromSameTask(this);
            }
        } else {
            switch (pageNumber) {
                case 0:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_no_locus)));
                    imageView.setImageResource(android.R.color.transparent);
                    previousButton.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_enter_formulas_and_send)));
                    imageView.setImageResource(R.drawable.enter_formulas_and_send);
                    previousButton.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_select_app_for_send_gpx)));
                    imageView.setImageResource(R.drawable.choose_filemanager);
                    break;
                case 3:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_open_with_other_app)));
                    imageView.setImageResource(R.drawable.open_with_cgeo);
                    break;
                case 4:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_other_apps_import)));
                    imageView.setImageResource(R.drawable.import_to_cgeo);
                    nextButton.setText(R.string.string_intro_button_next);
                    skipButton.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_other_app_shows_points)));
                    imageView.setImageResource(R.drawable.view_in_cgeo);
                    nextButton.setText(R.string.string_intro_button_ok);
                    skipButton.setVisibility(View.INVISIBLE);
                    break;
                default:
                    pageNumber = 0;
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
                pageNumber = 0;
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
