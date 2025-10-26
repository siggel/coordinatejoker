/*
 * Copyright (c) 2018-2025 by siggel <siggel-apps@gmx.de>
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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import static android.content.ClipDescription.MIMETYPE_TEXT_HTML;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import java.util.Objects;

public class ImportActivity extends AppCompatActivity {

    private final int DARK_GREEN = 0xFF00AA00;
    private final int RED = Color.RED;
    private final CoordinateParser coordinateParser = new CoordinateParser();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        connectToUiElements();
        addListeners();
        editText.post(this::importClipboard);
    }

    private void importClipboard() {
        // copy clipboard to input field and try to parse it
        String clipboardContent = "";
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // NullPointerException is handled
            if (Objects.requireNonNull(clipboard.getPrimaryClipDescription()).hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                // NullPointerException is handled
                ClipData.Item item = Objects.requireNonNull(clipboard.getPrimaryClip()).getItemAt(0);
                clipboardContent = item.getText().toString();
            } else if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_HTML)) {
                // NullPointerException is handled
                ClipData.Item item = Objects.requireNonNull(clipboard.getPrimaryClip()).getItemAt(0);
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

    private void addListeners() {
        // add change listener for editable input field trying to parse the input
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                processInput();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
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

    @SuppressLint("SetTextI18n") // no i18n for ° ' N S E W required
    private void fillGuiFromModel() {
        MainModel model = coordinateParser.getModel();
        textViewNorth.setText(model.getNorth() ? "N" : "S");
        textViewDegreesNorth.setText(model.getDegreesNorth() + "°");
        textViewMinutesNorth.setText(model.getMinutesNorth() + "'");
        textViewEast.setText(model.getEast() ? "E" : "W");
        textViewDegreesEast.setText(model.getDegreesEast() + "°");
        textViewMinutesEast.setText(model.getMinutesEast() + "'");
        textViewDistance.setText(model.getDistance() + (model.getFeet() ? " ft" : " m"));
        textViewAzimuth.setText(model.getAzimuth() + "° TN");

        showReplacementInfo();

        if (coordinateParser.isModelValid()) {
            colorizeResult(DARK_GREEN);
            buttonUseResult.setEnabled(true);
        } else {
            colorizeResult(RED);
            buttonUseResult.setEnabled(false);
        }
    }

    private void colorizeResult(int color) {
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

    private void showReplacementInfo() {
        String variableReplacementInfo = "";
        if (coordinateParser.isModelValid()) {
            textViewReplacementInfo.setVisibility(View.VISIBLE);
            String replacementString = coordinateParser.getReplacementInfo();
            if (!replacementString.isEmpty()) {
                variableReplacementInfo = getString(R.string.string_replacement_info) + " " +
                        replacementString;
                textViewReplacementInfo.setTextColor(DARK_GREEN);
            } else {
                variableReplacementInfo = getString(R.string.string_no_replacement_info);
                textViewReplacementInfo.setTextColor(RED);
            }
        } else {
            textViewReplacementInfo.setVisibility(View.INVISIBLE);
        }
        textViewReplacementInfo.setText(variableReplacementInfo);
    }

    /**
     * method called when user clicked the cancel button
     *
     * @param view view just syntactically needed here
     */
    public void cancelImport(@SuppressWarnings({"unused", "RedundantSuppression"}) View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * method called when user clicked the use result button
     *
     * @param view view just syntactically needed here
     */
    public void useResult(@SuppressWarnings({"unused", "RedundantSuppression"}) View view) {
        final Preferences preferences = new Preferences(this);
        preferences.saveFormulas(coordinateParser.getModel());
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
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
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
                findViewById(R.id.custom_toast_container));

        TextView textView = view.findViewById(R.id.text);
        textView.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }


    public void onRadioButtonClicked(View view) {
        int id = view.getId();
        if (id == R.id.importPreprocessLowerX) {
            coordinateParser.setLetterToBeReplacedByStar('x');
        } else if (id == R.id.importPreprocessDiagonalCross) {
            coordinateParser.setLetterToBeReplacedByStar('×');
        } else {
            coordinateParser.setLetterToBeReplacedByStar('*');
        }
        processInput();
    }

    private void processInput() {
        try {
            coordinateParser.processInput(editText.getText().toString());
            fillGuiFromModel();
        } catch (Exception e) {
            showError(getString(R.string.string_error_parsing_failed));
        }
    }
}
