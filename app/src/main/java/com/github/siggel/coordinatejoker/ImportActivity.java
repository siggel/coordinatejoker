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
    private final MainModel model;
    private String foundLetters;
    private EditText editText;
    private TextView textViewNorth;
    private TextView textViewDegreesNorth;
    private TextView textViewMinutesNorth;
    private TextView textViewEast;
    private TextView textViewDegreesEast;
    private TextView textViewMinutesEast;
    private TextView textViewProjectionTitle;
    private TextView textViewDistance;
    private TextView textViewAzimuth;
    private TextView textViewReplacementInfo;
    private Button buttonUseResult;
    private boolean replaceLowercaseXByStar = false;
    private boolean replaceDiagonalCrossByStar = false;

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

        connectToUiElements();

        // add change listener for editable input field trying to parse the input
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                processInput();
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
    }

    private void connectToUiElements() {
        // shortcuts to ui elements
        editText = findViewById(R.id.clipboardContent);
        textViewNorth = findViewById(R.id.importResultNorth);
        textViewDegreesNorth = findViewById(R.id.importResultDegreesNorth);
        textViewMinutesNorth = findViewById(R.id.importResultMinutesNorth);
        textViewEast = findViewById(R.id.importResultEast);
        textViewDegreesEast = findViewById(R.id.importResultDegreesEast);
        textViewMinutesEast = findViewById(R.id.importResultMinutesEast);
        textViewProjectionTitle = findViewById(R.id.importResultProjectionTitle);
        textViewDistance = findViewById(R.id.importResultDistance);
        textViewAzimuth = findViewById(R.id.importResultAzimuth);
        textViewReplacementInfo = findViewById(R.id.importReplacementInfo);
        buttonUseResult = findViewById(R.id.useButton);
    }

    private String orderLikeTargetLetters(String letters) {
        // if letters contains first target letter, bring it to the front
        letters = letters.replaceAll(
                "(.*)(" + String.valueOf(targetLetters.charAt(0)) + ")(.*)",
                "$2$1$3");
        // if letters contains second target letter, bring it to the end
        letters = letters.replaceAll(
                "(.*)(" + String.valueOf(targetLetters.charAt(1)) + ")(.*)",
                "$1$3$2");
        return letters;
    }

    private void tryToReplaceVariables() {
        // number of distinct letters in degrees' and minutes' formulas
        foundLetters = extractDistinctLetters(
                model.getDegreesNorth() + model.getMinutesNorth()
                        + model.getDegreesEast() + model.getMinutesEast());

        // replace letters (variables) with x, y if no more than two letters were found
        if (foundLetters.length() <= 2) {

            // it is mandatory to order the found letters, so that we do not replace a>>x, then x>>y
            foundLetters = orderLikeTargetLetters(foundLetters);

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
        final String formulaPattern = "[\\w.\\s+\\-*/^()]*"; // alphanumeric.space+-*/^()
        final String northDegreePattern = "([N,S])(" + formulaPattern + ")°?";
        final String eastDegreePattern = "([E,W])(" + formulaPattern + ")°?";
        final String minutesPattern = "(" + formulaPattern + ")'?";
        final String distancePattern = "(" + formulaPattern + ")(m|ft)";
        final String azimuthPattern = "(" + formulaPattern + ")°\\s*TN";
        final String spacePattern = "\\s*";
        final String spaceOrCommaPattern = "[\\s,]*";
        final Pattern patternIncludingProjection = Pattern.compile(
                northDegreePattern + spacePattern + minutesPattern + spaceOrCommaPattern +
                        eastDegreePattern + spacePattern + minutesPattern + spaceOrCommaPattern +
                        distancePattern + spacePattern + azimuthPattern,
                Pattern.DOTALL);
        final Pattern patternWithoutProjection = Pattern.compile(
                northDegreePattern + spacePattern + minutesPattern + spaceOrCommaPattern +
                        eastDegreePattern + spacePattern + minutesPattern,
                Pattern.DOTALL);

        Matcher matcher = patternIncludingProjection.matcher(input);
        boolean matched = matcher.find();
        if (!matched) {
            // can we recognize coordinates without a projection?
            matcher = patternWithoutProjection.matcher(input);
            matched = matcher.find();
        }
        int matches = matched ? matcher.groupCount() : 0;

        // store extracted values in result model
        if (matches >= 6) {
            // we found at least coordinates
            model.setNorth(matcher.group(1).trim().equals("N"));
            model.setDegreesNorth(matcher.group(2).trim());
            model.setMinutesNorth(matcher.group(3).trim());
            model.setEast(!matcher.group(4).trim().equals("W"));
            model.setDegreesEast(matcher.group(5).trim());
            model.setMinutesEast(matcher.group(6).trim());
            if (matches == 9) {
                // we also found projection data
                model.setDistance(matcher.group(7).trim());
                model.setFeet(matcher.group(8).trim().equals("ft"));
                model.setAzimuth(matcher.group(9).trim());
            } else {
                model.setDistance("0");
                model.setFeet(false);
                model.setAzimuth("0");
            }
        } else {
            // mark invalid in result model
            fillModelWithInvalidValues();
        }

    }

    private void fillModelWithInvalidValues() {
        model.setNorth(true);
        model.setDegreesNorth(INVALID);
        model.setMinutesNorth(INVALID);
        model.setEast(true);
        model.setDegreesEast(INVALID);
        model.setMinutesEast(INVALID);
        model.setDistance(INVALID);
        model.setAzimuth(INVALID);
    }

    private String preprocessMultiplicationOperator(String input) {
        if (replaceLowercaseXByStar) {
            input = input.replaceAll("x", "*");
        } else if (replaceDiagonalCrossByStar) {
            input = input.replaceAll("×", "*");
        }
        return input;
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
        textViewNorth.setText(model.getNorth() ? "N" : "S");
        textViewDegreesNorth.setText(model.getDegreesNorth() + "°");
        textViewMinutesNorth.setText(model.getMinutesNorth() + "'");
        textViewEast.setText(model.getEast() ? "E" : "W");
        textViewDegreesEast.setText(model.getDegreesEast() + "°");
        textViewMinutesEast.setText(model.getMinutesEast() + "'");
        textViewDistance.setText(model.getDistance() + (model.getFeet() ? " ft" : " m"));
        textViewAzimuth.setText(model.getAzimuth() + "° TN");

        if (!model.getMinutesEast().equals(INVALID)) {
            colorifyResult(DARK_GREEN);
            showReplacementInfo(true);
            buttonUseResult.setEnabled(true);
        } else {
            colorifyResult(RED);
            showReplacementInfo(false);
            buttonUseResult.setEnabled(false);
        }
    }

    private void colorifyResult(int color) {
        textViewNorth.setTextColor(color);
        textViewDegreesNorth.setTextColor(color);
        textViewMinutesNorth.setTextColor(color);
        textViewEast.setTextColor(color);
        textViewDegreesEast.setTextColor(color);
        textViewMinutesEast.setTextColor(color);
        textViewProjectionTitle.setTextColor(color);
        textViewDistance.setTextColor(color);
        textViewAzimuth.setTextColor(color);
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


    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.importPreprocessLowerX:
                replaceLowercaseXByStar = true;
                replaceDiagonalCrossByStar = false;
                break;
            case R.id.importPreprocessDiagonalCross:
                replaceDiagonalCrossByStar = true;
                replaceLowercaseXByStar = false;
                break;
            default:
                replaceLowercaseXByStar = false;
                replaceDiagonalCrossByStar = false;
                break;
        }
    }

    private void processInput() {
        String result;
        result = preprocessMultiplicationOperator(editText.getText().toString());
        parseString(result);
        tryToReplaceVariables();
        fillGuiFromModel();
    }
}
