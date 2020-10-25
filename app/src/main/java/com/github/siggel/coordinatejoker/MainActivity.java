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
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

/**
 * class for showing main application page
 */
public class MainActivity extends AppCompatActivity {

    /**
     * model holding all values of the ui
     */
    private MainModel mainModel;

    /**
     * constructor
     */
    public MainActivity() {
        mainModel = new MainModel();
    }

    /**
     * Android onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load formulas and settingsModel on create
        this.restore();

        // set text from html again to support links
        ((TextView) findViewById(R.id.mainTextViewIntro)).setText(Html.fromHtml(getString(R.string.htmlstring_intro)));
        ((TextView) findViewById(R.id.mainTextViewIntro)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.checkboxExplanationLink)).setText(Html.fromHtml(getString(R.string.htmlstring_checkbox_explanation_link)));
        ((TextView) findViewById(R.id.checkboxExplanationLink)).setMovementMethod(LinkMovementMethod.getInstance());

        // set focus to top left element (except North/South selector)
        findViewById(R.id.degreesNorthFormula).requestFocus();

        // programmatically add icons to buttons (as it does not work from xml for pre-Lollipop)
        boolean useActionViewIntent = !PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.key_share), false);
        setLeftDrawableOfTextView(findViewById(R.id.resetButton),
                R.drawable.reset_icon);
        setLeftDrawableOfTextView(findViewById(R.id.sendButton),
                useActionViewIntent ? R.drawable.view_icon : R.drawable.share_icon);

        // adapt button text according to configured export behavior
        ((Button) findViewById(R.id.sendButton)).setText(
                useActionViewIntent ? R.string.string_view : R.string.string_send);

        // set title icon manually as it does not work for this theme from manifest xml
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.mipmap.ic_title);
        }

        // check if we run a new version for the first time
        int currentVersion = getCurrentVersion();
        int previousVersion = getPreviousVersion();

        if (previousVersion < 13) {
            // very first startup

            rememberVersion();

            // auto-configure for locus or cgeo
            autoConfigureExportSettings();

            // preset formulas with example values
            mainModel.setExampleValues();
            fillGuiFromModel();

            // show intro
            startActivity(new Intent(this, IntroActivity.class));
        } else if (currentVersion > previousVersion) {
            rememberVersion();

            // display change notes
            Intent intent = new Intent(this, ChangeHistoryActivity.class);
            intent.putExtra("currentVersion", currentVersion);
            intent.putExtra("previousVersion", previousVersion);
            startActivity(intent);
        }
    }

    /**
     * auto configuration to be called if app has never run before
     */
    private void autoConfigureExportSettings() {
        final Preferences preferences = new Preferences(this);
        preferences.autoConfigureExportSettings();
    }

    /**
     * method for adding text view's left drawable programmatically
     *
     * @param textView   text view to be extended with drawable
     * @param drawableId drawable's id
     */
    private void setLeftDrawableOfTextView(TextView textView, int drawableId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = AppCompatResources
                    .getDrawable(this, drawableId);
        } else {
            drawable = VectorDrawableCompat
                    .create(this.getResources(), drawableId, null);
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(
                drawable, null, null, null);
    }

    /**
     * Android onStop method
     */
    @Override
    protected void onStop() {

        // save formulas on stop
        this.store();

        super.onStop();
    }

    /**
     * load formulas from preferences
     */
    private void restore() {
        final Preferences preferences = new Preferences(this);
        mainModel = preferences.loadFormulas();
        fillGuiFromModel();
    }

    /**
     * save formulas to preferences
     */
    private void store() {
        final Preferences preferences = new Preferences(this);
        fillModelFromGui();
        preferences.saveFormulas(mainModel);
    }

    /**
     * fill gui with values from mainModel
     */
    private void fillGuiFromModel() {
        // to become language independent, the convention for the spinners is, that first entries
        // are North and East
        ((Spinner) findViewById(R.id.spinnerNorth)).setSelection(mainModel.getNorth() ? 0 : 1);
        ((Spinner) findViewById(R.id.spinnerEast)).setSelection(mainModel.getEast() ? 0 : 1);
        ((EditText) findViewById(R.id.degreesNorthFormula)).setText(mainModel.getDegreesNorth());
        ((EditText) findViewById(R.id.degreesEastFormula)).setText(mainModel.getDegreesEast());
        ((EditText) findViewById(R.id.minutesNorthFormula)).setText(mainModel.getMinutesNorth());
        ((EditText) findViewById(R.id.minutesEastFormula)).setText(mainModel.getMinutesEast());
        ((CheckBox) findViewById(R.id.doReplaceMinutes)).setChecked(mainModel.getDoReplaceMinutes());
        ((EditText) findViewById(R.id.distanceFormula)).setText(mainModel.getDistance());
        ((Spinner) findViewById(R.id.spinnerUnits)).setSelection(mainModel.getFeet() ? 0 : 1);
        ((EditText) findViewById(R.id.azimuthFormula)).setText(mainModel.getAzimuth());
        ((EditText) findViewById(R.id.xValues)).setText(mainModel.getXRange());
        ((EditText) findViewById(R.id.yValues)).setText(mainModel.getYRange());
    }

    /**
     * fill mainModel with values from gui
     */
    private void fillModelFromGui() {
        // to become language independent, the convention for the spinners is, that first entries
        // are North and East
        mainModel.setNorth(
                ((Spinner) findViewById(R.id.spinnerNorth)).getSelectedItemPosition() == 0);
        mainModel.setEast(
                ((Spinner) findViewById(R.id.spinnerEast)).getSelectedItemPosition() == 0);
        mainModel.setDegreesNorth(
                ((EditText) findViewById(R.id.degreesNorthFormula)).getText().toString());
        mainModel.setDegreesEast(
                ((EditText) findViewById(R.id.degreesEastFormula)).getText().toString());
        mainModel.setMinutesNorth(
                ((EditText) findViewById(R.id.minutesNorthFormula)).getText().toString());
        mainModel.setMinutesEast(
                ((EditText) findViewById(R.id.minutesEastFormula)).getText().toString());
        mainModel.setDoReplaceMinutes(
                ((CheckBox) findViewById(R.id.doReplaceMinutes)).isChecked());
        mainModel.setDistance(
                ((EditText) findViewById(R.id.distanceFormula)).getText().toString());
        mainModel.setFeet(
                ((Spinner) findViewById(R.id.spinnerUnits)).getSelectedItemPosition() == 0);
        mainModel.setAzimuth(
                ((EditText) findViewById(R.id.azimuthFormula)).getText().toString());
        mainModel.setXRange(
                ((EditText) findViewById(R.id.xValues)).getText().toString());
        mainModel.setYRange(
                ((EditText) findViewById(R.id.yValues)).getText().toString());
    }

    /**
     * method called when user clicked the reset button
     *
     * @param view view just syntactically needed here
     */
    public void resetFields(@SuppressWarnings("unused") View view) {
        mainModel.reset();
        fillGuiFromModel();
    }

    /**
     * method called when the user clicked the send button
     *
     * @param view view just syntactically needed here
     */
    public void sendMessage(@SuppressWarnings("unused") View view) {
        try {
            fillModelFromGui();

            final int requestedNumberOfPoints = Math.max(1,
                    IntegerRange.getValues(this, mainModel.getXRange()).size())
                    * Math.max(1,
                    IntegerRange.getValues(this, mainModel.getYRange()).size());

            // keep number of generated points in a reasonable range
            if (requestedNumberOfPoints > 100) {
                showError(getString(R.string.string_too_many_points));
                return;
            }

            // solve formulas for requested x-values
            Calculator calculator = new Calculator(this,
                    mainModel);
            List<Point> waypoints = calculator.solve();

            if (waypoints.size() == 0) {
                showError(getString(R.string.string_empty_waypoints));
                // nothing available for export
                return;
            } else if (waypoints.size() < requestedNumberOfPoints) {
                showWarning(getString(R.string.string_some_invalid_waypoints));
            }

            // determine export format from preferences
            ExportSettings exportSettings = new ExportSettings();
            String app = Objects.requireNonNull(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(getString(R.string.key_use_with), "locus"));
            if ("expert".equals(app)) {
                exportSettings.setWantsToShare(PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean(getString(R.string.key_share), false));
                exportSettings.setUseMimeType(PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean(getString(R.string.key_use_mime), true));
                exportSettings.setFormat(PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.key_format), "gpx"));
            } else {
                exportSettings = new ExportSettings(app);
            }

            store();

            // export
            Exporter exporter = ExporterFactory.getExporter(this, exportSettings);
            exporter.export(waypoints);

        } catch (Exception e) {
            // we should be prepared to get CalculatorException, ExporterException or ParseException
            // here, this would be an error resulting in no solution
            showError(e.getMessage());
        }
    }

    /**
     * method called when the options menu is created
     *
     * @param menu as defined by android
     * @return boolean as defined by android
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * method called when user selected an item from the options menu
     *
     * @param item as defined by android
     * @return boolean as defined by android
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // be sure to save preferences before switching to another activity
        this.store();

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_intro:
                startActivity(new Intent(this, IntroActivity.class));
                break;
            case R.id.action_change_history:
                // intent without extra previousVersion, i.e. from the beginning
                Intent intent = new Intent(this, ChangeHistoryActivity.class);
                intent.putExtra("currentVersion", getCurrentVersion());
                startActivity(intent);
                break;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * method for displaying messages in a custom toast in case of errors
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

    /**
     * method for displaying messages in a custom toast in case of warnings
     *
     * @param message message to be displayed
     */
    private void showWarning(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_warning_toast,
                findViewById(R.id.custom_toast_container));

        TextView textView = view.findViewById(R.id.text);
        textView.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    /**
     * get current version code of app
     *
     * @return version code
     */
    private int getCurrentVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * get version code of previous app run (stored in preferences)
     *
     * @return version code
     */
    private int getPreviousVersion() {
        final Preferences preferences = new Preferences(this);
        return preferences.loadVersionCode();
    }

    /**
     * store current version in preferences
     */
    private void rememberVersion() {
        final Preferences preferences = new Preferences(this);
        preferences.saveVersionCode(getCurrentVersion());
    }

}
