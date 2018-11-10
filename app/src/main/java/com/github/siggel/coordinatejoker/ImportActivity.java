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
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ClipDescription.MIMETYPE_TEXT_HTML;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ImportActivity extends AppCompatActivity {

    private final String INVALID = "???"; // descriptive string indicating an invalid formula/range
    private final String targetLetters = "xy"; // variables x and y are supported
    private final int DARK_GREEN = 0xFF00AA00;
    private final int RED = Color.RED;
    private String foundLetters;
    private final MainModel model;
    private EditText editText;
    private TextView textViewNorth;
    private TextView textViewDegreesNorth;
    private TextView textViewMinutesNorth;
    private TextView textViewEast;
    private TextView textViewDegreesEast;
    private TextView textViewMinutesEast;
    private TextView textViewReplacementInfo;
    private Button buttonUseResult;

    public ImportActivity() {
        model = new MainModel();

        // set defaults
        model.setNorth(true);
        model.setDegreesNorth(INVALID);
        model.setMinutesNorth(INVALID);
        model.setEast(true);
        model.setDegreesEast(INVALID);
        model.setMinutesEast(INVALID);
        model.setDistance("0");
        model.setFeet(false);
        model.setAzimuth("0");
        model.setXRange(INVALID); // fill x with invalid value, so user cannot forget to adjust it
        model.setYRange("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        // shortcuts to ui elements
        editText = findViewById(R.id.clipboardContent);
        textViewNorth = findViewById(R.id.importResultNorth);
        textViewDegreesNorth = findViewById(R.id.importResultDegreesNorth);
        textViewMinutesNorth = findViewById(R.id.importResultMinutesNorth);
        textViewEast = findViewById(R.id.importResultEast);
        textViewDegreesEast = findViewById(R.id.importResultDegreesEast);
        textViewMinutesEast = findViewById(R.id.importResultMinutesEast);
        textViewReplacementInfo = findViewById(R.id.importReplacementInfo);
        buttonUseResult = findViewById(R.id.useButton);

        // add change listener for editable input field trying to parse the input
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                parseString(editText.getText().toString());
                tryToReplaceVariables();
                fillGuiFromModel();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // copy clipboard to input field and try to parse it
        String clipboardContent = "";
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            //noinspection ConstantConditions // NullPointerException is handled
            if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                //noinspection ConstantConditions // NullPointerException is handled
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                clipboardContent = item.getText().toString();
            } else if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_HTML)) {
                //noinspection ConstantConditions // NullPointerException is handled
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                clipboardContent = Html.fromHtml(item.getHtmlText()).toString();
            } else {
                showError(getString(R.string.string_error_clipboard_failed));
            }

            // avoid processing unreasonably long coordinate formulas
            if (clipboardContent.length() > 256) {
                clipboardContent = "";
                showError(getString(R.string.string_error_big_clipboard));
            }
        } catch (Exception e) {
            // leave clipboardContent empty if an exception occurred during clipboard reading
            showError(getString(R.string.string_error_clipboard_failed));
        }

        editText.setText(clipboardContent);
        parseString(clipboardContent);
        tryToReplaceVariables();
        fillGuiFromModel();
    }

    private void tryToReplaceVariables() {
        // number of distinct letters in degrees' and minutes' formulas
        foundLetters = extractDistinctLetters(
                model.getDegreesNorth() + model.getMinutesNorth()
                        + model.getDegreesEast() + model.getMinutesEast());

        // replace letters (variables) with x, y if no more than two letters were found
        if (foundLetters.length() <= 2) {
            for (int i = 0; i < foundLetters.length(); ++i) {
                model.setDegreesNorth(
                        model.getDegreesNorth().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setMinutesNorth(
                        model.getMinutesNorth().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setDegreesEast(
                        model.getDegreesEast().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setMinutesEast(
                        model.getMinutesEast().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
            }
        }

        // fill y with invalid value if more than one variable is used, so user cannot forget it
        if (foundLetters.length() >= 2) {
            model.setYRange(INVALID);
        } else {
            model.setYRange("");
        }
    }

    private void parseString(String input) {
        final String pattern = "([N,S])([^°]*)°([^']*)'?.*([E,W])([^°]*)°([^']*)'?";
        Matcher matcher = Pattern.compile(pattern).matcher(input);
        if (matcher.find()) {
            // store extracted values in result model
            model.setNorth(matcher.group(1).trim().equals("N"));
            model.setDegreesNorth(matcher.group(2).trim());
            model.setMinutesNorth(matcher.group(3).trim());
            model.setEast(!matcher.group(4).trim().equals("W"));
            model.setDegreesEast(matcher.group(5).trim());
            model.setMinutesEast(matcher.group(6).trim());
        } else {
            // mark invalid in result model
            model.setNorth(true);
            model.setDegreesNorth(INVALID);
            model.setMinutesNorth(INVALID);
            model.setEast(true);
            model.setDegreesEast(INVALID);
            model.setMinutesEast(INVALID);
        }
    }

    private String extractDistinctLetters(String input) {
        // remove all but letters (variables and function names remain)
        String cleanedInput = input.replaceAll("[^A-Za-z]", "");
        StringBuilder extractedLetters = new StringBuilder();
        for (int i = 0; i < cleanedInput.length(); ++i) {
            if (!extractedLetters.toString().contains(String.valueOf(cleanedInput.charAt(i)))) {
                extractedLetters.append(cleanedInput.charAt(i));
            }
        }
        return extractedLetters.toString();
    }

    @SuppressLint("SetTextI18n") // no i18n for ° ' N S E W required
    private void fillGuiFromModel() {
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
            showReplacementInfo(true);
            buttonUseResult.setEnabled(true);
        } else {
            textViewNorth.setText(INVALID);
            textViewDegreesNorth.setText(INVALID);
            textViewMinutesNorth.setText(INVALID);
            textViewEast.setText(INVALID);
            textViewDegreesEast.setText(INVALID);
            textViewMinutesEast.setText(INVALID);
            textViewNorth.setTextColor(RED);
            textViewDegreesNorth.setTextColor(RED);
            textViewMinutesNorth.setTextColor(RED);
            textViewEast.setTextColor(RED);
            textViewDegreesEast.setTextColor(RED);
            textViewMinutesEast.setTextColor(RED);
            showReplacementInfo(false);
            buttonUseResult.setEnabled(false);
        }
    }

    private void showReplacementInfo(boolean resultIsValid) {
        if (resultIsValid) {
            String variableReplacementInfo = "";
            if (foundLetters.length() <= 2) {
                variableReplacementInfo += getString(R.string.string_replacement_info);
                if (foundLetters.length() >= 1) {
                    variableReplacementInfo += " ";
                    variableReplacementInfo += foundLetters.charAt(0) + " >> "
                            + targetLetters.charAt(0);
                }
                if (foundLetters.length() == 2) {
                    variableReplacementInfo += ", ";
                    variableReplacementInfo += foundLetters.charAt(1) + " >> "
                            + targetLetters.charAt(1);
                }
                textViewReplacementInfo.setTextColor(DARK_GREEN);

            } else {
                variableReplacementInfo += getString(R.string.string_no_replacement_info);
                textViewReplacementInfo.setTextColor(RED);

            }
            textViewReplacementInfo.setText(variableReplacementInfo);
            textViewReplacementInfo.setVisibility(View.VISIBLE);
        } else {
            textViewReplacementInfo.setVisibility(View.INVISIBLE);
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

    /**
     * method for displaying messages in a custom toast in case of warnings
     *
     * @param message message to be displayed
     */
    private void showError(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_error_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView textView = view.findViewById(R.id.text);
        textView.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }


}
