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

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ImportActivity extends AppCompatActivity {

    private final String INVALID = "???";
    private final MainModel model;
    private EditText editText;
    private TextView textViewNorth;
    private TextView textViewDegreesNorth;
    private TextView textViewMinutesNorth;
    private TextView textViewEast;
    private TextView textViewDegreesEast;
    private TextView textViewMinutesEast;
    private Button buttonUseResult;

    public ImportActivity() {
        model = new MainModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        editText = findViewById(R.id.clipboardContent);
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                parseString(editText.getText().toString());
                fillGuiFromModel();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        textViewNorth = findViewById(R.id.importResultNorth);
        textViewDegreesNorth = findViewById(R.id.importResultDegreesNorth);
        textViewMinutesNorth = findViewById(R.id.importResultMinutesNorth);
        textViewEast = findViewById(R.id.importResultEast);
        textViewDegreesEast = findViewById(R.id.importResultDegreesEast);
        textViewMinutesEast = findViewById(R.id.importResultMinutesEast);
        buttonUseResult = findViewById(R.id.useButton);


        String clipboardContent = "";
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            //noinspection ConstantConditions // NullPointerException is handled
            if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                //noinspection ConstantConditions // NullPointerException is handled
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                clipboardContent = item.getText().toString();
            }
        } catch (NullPointerException e) {
            // leave clipboardContent empty if an exception occurred during clipboard reading
        }
        editText.setText(clipboardContent);
        parseString(clipboardContent);
        fillGuiFromModel();
    }

    private void parseString(String input) {
        final String pattern = "([N,S])([^°]*)°([^']*)'?.*([E,W])([^°]*)°([^']*)'?";
        Matcher matcher = Pattern.compile(pattern).matcher(input);
        if (matcher.find()) {
            model.setNorth(matcher.group(1).trim().equals("N"));
            model.setDegreesNorth(matcher.group(2).trim());
            model.setMinutesNorth(matcher.group(3).trim());
            model.setEast(!matcher.group(4).trim().equals("W"));
            model.setDegreesEast(matcher.group(5).trim());
            model.setMinutesEast(matcher.group(6).trim());
            model.setDistance("0");
            model.setFeet(false);
            model.setAzimuth("0");
            model.setXRange("0-9");
            model.setYRange("");
            buttonUseResult.setEnabled(true);
        } else {
            model.setNorth(true);
            model.setDegreesNorth(INVALID);
            model.setMinutesNorth(INVALID);
            model.setEast(true);
            model.setDegreesEast(INVALID);
            model.setMinutesEast(INVALID);
            buttonUseResult.setEnabled(false);
        }
    }

    @SuppressLint("SetTextI18n") // no i18n for ° ' N S E W required
    private void fillGuiFromModel() {
        final int DARK_GREEN = 0xFF00AA00;
        if (!model.getMinutesEast().equals(INVALID)) {
            textViewNorth.setText(model.getNorth() ? "N" : "S");
            textViewDegreesNorth.setText(model.getDegreesNorth() + "°");
            textViewMinutesNorth.setText(model.getMinutesNorth() + "'");
            textViewEast.setText(model.getEast() ? "E" : "W");
            textViewDegreesEast.setText(model.getDegreesEast() + "°");
            textViewMinutesEast.setText(model.getMinutesEast() + "'");
            textViewNorth.setTextColor(DARK_GREEN);
            textViewDegreesNorth.setTextColor(DARK_GREEN);
            textViewMinutesNorth.setTextColor(DARK_GREEN);
            textViewEast.setTextColor(DARK_GREEN);
            textViewDegreesEast.setTextColor(DARK_GREEN);
            textViewMinutesEast.setTextColor(DARK_GREEN);
        } else {
            textViewNorth.setText(INVALID);
            textViewDegreesNorth.setText(INVALID);
            textViewMinutesNorth.setText(INVALID);
            textViewEast.setText(INVALID);
            textViewDegreesEast.setText(INVALID);
            textViewMinutesEast.setText(INVALID);
            textViewNorth.setTextColor(Color.RED);
            textViewDegreesNorth.setTextColor(Color.RED);
            textViewMinutesNorth.setTextColor(Color.RED);
            textViewEast.setTextColor(Color.RED);
            textViewDegreesEast.setTextColor(Color.RED);
            textViewMinutesEast.setTextColor(Color.RED);
        }
    }

    /**
     * method called when user clicked the cancel button
     *
     * @param view view just syntactically needed here
     */
    public void cancelImport(@SuppressWarnings("unused") View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * method called when user clicked the use result button
     *
     * @param view view just syntactically needed here
     */
    public void useResult(@SuppressWarnings("unused") View view) {
        final Preferences preferences = new Preferences(this);
        preferences.saveFormulas(model);
        NavUtils.navigateUpFromSameTask(this);
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

}
