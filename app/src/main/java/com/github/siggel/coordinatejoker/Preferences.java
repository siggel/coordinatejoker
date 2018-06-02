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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * class for storing/loading ui formulas and settings to/from app's preferences
 */
class Preferences {

    /**
     * the app's main context required for accessing preferences
     */
    private final Context context;

    /**
     * constructor
     *
     * @param context the app's main context required for accessing preferences
     */
    Preferences(Context context) {
        this.context = context;
    }

    /**
     * method for loading formulas from preferences
     *
     * @return model filled with formulas stored at last save
     */
    MainModel loadFormulas() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        MainModel mainModel = new MainModel();
        mainModel.setNorth(
                sharedPref.getBoolean(context.getString(R.string.save_is_north), true));
        mainModel.setDegreesNorth(
                sharedPref.getString(context.getString(R.string.save_degrees_north), ""));
        mainModel.setMinutesNorth(
                sharedPref.getString(context.getString(R.string.save_minutes_north), ""));
        mainModel.setEast(
                sharedPref.getBoolean(context.getString(R.string.save_is_east), true));
        mainModel.setDegreesEast(
                sharedPref.getString(context.getString(R.string.save_degrees_east), ""));
        mainModel.setMinutesEast(
                sharedPref.getString(context.getString(R.string.save_minutes_east), ""));
        mainModel.setDistance(
                sharedPref.getString(context.getString(R.string.save_distance), "0"));
        mainModel.setFeet(
                sharedPref.getBoolean(context.getString(R.string.save_is_feet), false));
        mainModel.setAzimuth(
                sharedPref.getString(context.getString(R.string.save_azimuth), "0"));
        mainModel.setXFrom(
                Integer.parseInt(sharedPref.getString(context.getString(R.string.save_x_from), "0")));
        mainModel.setXTo(
                Integer.parseInt(sharedPref.getString(context.getString(R.string.save_x_to), "9")));
        return mainModel;
    }

    /**
     * method for saving current formulas to preferences
     *
     * @param mainModel mainModel with formulas to be saved
     */
    void saveFormulas(MainModel mainModel) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.save_is_north), mainModel.getNorth());
        editor.putString(context.getString(R.string.save_degrees_north), mainModel.getDegreesNorth());
        editor.putString(context.getString(R.string.save_minutes_north), mainModel.getMinutesNorth());
        editor.putBoolean(context.getString(R.string.save_is_east), mainModel.getEast());
        editor.putString(context.getString(R.string.save_degrees_east), mainModel.getDegreesEast());
        editor.putString(context.getString(R.string.save_minutes_east), mainModel.getMinutesEast());
        editor.putString(context.getString(R.string.save_distance), mainModel.getDistance());
        editor.putBoolean(context.getString(R.string.save_is_feet), mainModel.getFeet());
        editor.putString(context.getString(R.string.save_azimuth), mainModel.getAzimuth());
        editor.putString(context.getString(R.string.save_x_from), mainModel.getXFrom().toString());
        editor.putString(context.getString(R.string.save_x_to), mainModel.getXTo().toString());
        editor.apply();
    }

    int loadVersionCode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(context.getString(R.string.save_version_code), -1);
    }

    void saveVersionCode(int versionCode) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.save_version_code), versionCode);
        editor.apply();
    }

}
