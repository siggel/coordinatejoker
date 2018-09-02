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
import android.os.Bundle;

import java.io.IOException;

/**
 * Activity showing change history page
 * <p>
 * If the intent provides previousVersion only the delta to previous version will be shown
 */
public class ChangeHistoryActivity extends VersatileWebViewActivity {

    /**
     * Android onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final int previousVersion = intent.getIntExtra("previousVersion", 0);
        boolean haveNothingToShow = true;

        StringBuilder html = new StringBuilder("");

        // header
        try {
            html.append(FileHelper.readContentFromInputStream(
                    getAssets().open("changes_header_" +
                            getString(R.string.string_html_page_language_id) + ".html")));
        } catch (IOException e) {
            // ignore
        }

        // add relevant change notes
        if (previousVersion < 15) {
            try {
                html.append(FileHelper.readContentFromInputStream(
                        getAssets().open("changes_version_15_" +
                                getString(R.string.string_html_page_language_id) + ".html")));
                haveNothingToShow = false;
            } catch (IOException e) {
                // ignore
            }
        }

        if (previousVersion < 14) {
            try {
                html.append(FileHelper.readContentFromInputStream(
                        getAssets().open("changes_version_14_and_before_" +
                                getString(R.string.string_html_page_language_id) + ".html")));
                haveNothingToShow = false;
            } catch (IOException e) {
                // ignore
            }
        }

        // fallback text
        if (haveNothingToShow) {
            html.append(getString(R.string.htmlstring_no_change_history_found));
        }

        // footer
        html.append("</body></html>");

        webView.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "utf-8", null);
    }

}
