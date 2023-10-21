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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NavUtils;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Activity showing about page
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * Android onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setContentOfWebView();
        addIconsToButtons();
    }

    /**
     * method called when user clicks terms of service button
     *
     * @param view view just required syntactically here
     */
    public void openTermsOfService(@SuppressWarnings({"unused", "RedundantSuppression"}) View view) {
        startActivity(new Intent(this, TermsOfServiceActivity.class));
    }

    /**
     * method called when user clicks privacy policy button
     *
     * @param view view just required syntactically here
     */
    public void openPrivacyPolicy(@SuppressWarnings({"unused", "RedundantSuppression"}) View view) {
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
    }

    /**
     * method called when user clicks open source licenses button
     *
     * @param view view just required syntactically here
     */
    public void openOpenSourceLicenses(@SuppressWarnings({"unused", "RedundantSuppression"}) View view) {
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.title_activity_open_source_licenses));
    }

    public void openAcknowledgements(@SuppressWarnings({"unused", "RedundantSuppression"}) View view) {
        startActivity(new Intent(this, AcknowledgementsActivity.class));
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
        if (android.R.id.home == item.getItemId()) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setContentOfWebView() {
        WebView webView = findViewById(R.id.aboutWebView);
        String html = readFromAsset("about_"
                + getString(R.string.string_html_page_language_id)
                + ".html");
        html = insertCurrentVersion(html);
        webView.loadDataWithBaseURL(
                "file:///android_asset/",
                html, "text/html",
                "utf-8",
                null);
    }

    private void addIconsToButtons() {
        // programmatically add icons to buttons (as it does not work from xml for pre-Lollipop)
        setRightDrawableOfTextView(R.id.showTermsOfService, R.drawable.arrow_right_icon);
        setRightDrawableOfTextView(R.id.showPrivacyPolicy, R.drawable.arrow_right_icon);
        setRightDrawableOfTextView(R.id.showAcknowledgements, R.drawable.arrow_right_icon);
        setRightDrawableOfTextView(R.id.showOpenSourceLicenses, R.drawable.arrow_right_icon);
    }

    @NonNull
    private String insertCurrentVersion(@NonNull String html) {
        return html.replace("%VERSION%", getVersion());
    }

    @NonNull
    private String readFromAsset(@NonNull String fileName) {
        String html = "";
        try {
            InputStream input = getAssets().open(fileName);
            html = new Scanner(input).useDelimiter("\\A").next();
        } catch (IOException e) {
            // show an empty web view but continue showing at least the remaining elements
        }
        return html;
    }

    /**
     * method for adding text view's right drawable programmatically
     *
     * @param viewId     text view's id
     * @param drawableId drawable id
     */
    @SuppressWarnings("SameParameterValue")
    private void setRightDrawableOfTextView(int viewId, int drawableId) {
        Drawable drawable;
        drawable = AppCompatResources
                .getDrawable(this, drawableId);
        TextView textView = findViewById(viewId);
        textView.setCompoundDrawablesWithIntrinsicBounds(
                null, null, drawable, null);
    }

    /**
     * get app's version number
     *
     * @return app version as string
     */
    private String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

}
