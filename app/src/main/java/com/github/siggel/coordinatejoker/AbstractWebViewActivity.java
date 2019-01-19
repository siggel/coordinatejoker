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
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Base activity providing a versatile web view to be filled with specific content within derived
 * classes
 */
abstract class AbstractWebViewActivity extends AppCompatActivity {

    /**
     * WebView to be used in implementations
     */
    protected WebView webView;

    /**
     * abstract method to be implemented for setting webView's content
     */
    protected abstract void setContent();

    /**
     * onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versatile_webview);
        webView = findViewById(R.id.webView);
        enableLinkingBetweenAssetHtmls();
        setContent(); // implemented in derived class
    }

    /**
     * method called when back button is pressed
     * while home/up shall go back to previous activity, the back button shall not go back to parent
     * activity as long as we can go back within web view's pages
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void enableLinkingBetweenAssetHtmls() {
        // the following code serves as workaround for being able to link from one asset html to
        // another one
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

}
