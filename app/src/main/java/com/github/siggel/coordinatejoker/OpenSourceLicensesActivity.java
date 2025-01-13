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

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Activity showing open source licenses page
 */
public class OpenSourceLicensesActivity extends AbstractWebViewActivity {

    protected void setContent() {
        String html = readFromAsset("open_source_licenses_header_"
                + getString(R.string.string_html_page_language_id)
                + ".html");
        html = insertCurrentVersion(html);
        html += readFromAsset("sbom"
                + ".html");
        html += "\n</body>\n</html>";
        webView.loadDataWithBaseURL(
                "file:///android_asset/",
                html, "text/html",
                "utf-8",
                null);
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

    @NonNull
    private String insertCurrentVersion(@NonNull String html) {
        return html.replace("%VERSION%", getVersion());
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
