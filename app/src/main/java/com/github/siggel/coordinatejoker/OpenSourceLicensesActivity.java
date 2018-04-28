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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Activity showing open source licenses page
 */
public class OpenSourceLicensesActivity extends AppCompatActivity {

    /**
     * onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_source_licenses);

        final String licenseHtml = "<p>" + getString(R.string.string_open_source_usage) + "</p>"
                + getString(R.string.string_open_source_libraries_using_apache_2_0)
                + getString(R.string.string_license_text_apache_2_0);

        TextView textView = findViewById(R.id.openSourceLicenseTextView);
        textView.setText(Html.fromHtml(licenseHtml));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * method called when user clicked action bar's up/home button
     *
     * @param item as defined by android
     * @return boolean as defined by android
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
