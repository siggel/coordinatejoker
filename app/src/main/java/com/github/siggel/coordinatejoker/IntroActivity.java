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

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

/**
 * Activity showing help page
 */
public class IntroActivity extends AppCompatActivity {

    /**
     * current page number to be remembered if going through multiple onCreate (e.g. due to
     * switching between portrait and landscape)
     */
    private static int pageNumber = 0;

    /**
     * whether locus is selected in preferences in order not to check it again and again
     */
    private static boolean useLocus;

    /**
     * text element of page
     */
    private TextView textView;

    /**
     * image element of page
     */
    private ImageView imageView;

    /**
     * "next" button of page
     */
    private Button nextButton;

    /**
     * "previous" button of page
     */
    private Button previousButton;

    /**
     * "skip" button of page
     */
    private Button skipButton;

    /**
     * Android onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        useLocus = "locus".equals(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_use_with), ""));

        textView = findViewById(R.id.introTextView);
        imageView = findViewById(R.id.introImageView);
        nextButton = findViewById(R.id.introNextButton);
        previousButton = findViewById(R.id.introPreviousButton);
        skipButton = findViewById(R.id.introSkipButton);

        adjustPageContent();
    }

    /**
     * method called when user clicked the skip button
     *
     * @param view view just syntactically needed here
     */
    public void skipIntro(@SuppressWarnings({"unused", "RedundantSuppression"}) View view) {
        pageNumber = 0;
        NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * method called when user clicked the next button
     *
     * @param view view
     */
    public void next(View view) {
        try {
            int increment = Integer.parseInt(view.getTag().toString());
            pageNumber += increment;
            adjustPageContent();
        } catch (NumberFormatException e) {
            // should never happen
            skipIntro(view);
        }
    }

    /**
     * adjust page content according to current page number
     */
    private void adjustPageContent() {
        if (useLocus) {
            adjustPageContentForLocus();
        } else {
            adjustPageContentForCgeo();
        }
    }

    /**
     * adjust page content for Locus Map according to current page number
     */
    private void adjustPageContentForLocus() {

        switch (pageNumber) {
            case 0:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_have_locus)));
                imageView.setImageResource(android.R.color.transparent);
                previousButton.setVisibility(View.INVISIBLE);
                break;
            case 1:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_enter_formulas_and_view)));
                imageView.setImageResource(R.drawable.enter_formulas_and_view);
                previousButton.setVisibility(View.VISIBLE);
                break;
            case 2:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_locus_import)));
                imageView.setImageResource(R.drawable.import_to_locus);
                nextButton.setText(R.string.string_intro_button_next);
                skipButton.setVisibility(View.VISIBLE);
                break;
            case 3:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_locus_shows_points)));
                imageView.setImageResource(R.drawable.view_in_locus);
                nextButton.setText(R.string.string_intro_button_ok);
                skipButton.setVisibility(View.INVISIBLE);
                break;
            default:
                pageNumber = 0;
                NavUtils.navigateUpFromSameTask(this);
        }
    }

    /**
     * adjust page content for c:geo according to current page number
     */
    private void adjustPageContentForCgeo() {

        switch (pageNumber) {
            case 0:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_no_locus)));
                imageView.setImageResource(android.R.color.transparent);
                previousButton.setVisibility(View.INVISIBLE);
                break;
            case 1:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_enter_formulas_and_view)));
                imageView.setImageResource(R.drawable.enter_formulas_and_view);
                previousButton.setVisibility(View.VISIBLE);
                break;
            case 2:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_select_app_for_send_gpx)));
                imageView.setImageResource(R.drawable.select_app);
                break;
            case 3:
                textView.setText(Html.fromHtml(getString(R.string.htmlstring_intro_other_apps_import)));
                imageView.setImageResource(R.drawable.import_to_cgeo);
                nextButton.setText(R.string.string_intro_button_next);
                skipButton.setVisibility(View.VISIBLE);
                break;
            case 4:
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
        if (item.getItemId() == android.R.id.home) {
            pageNumber = 0;
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
