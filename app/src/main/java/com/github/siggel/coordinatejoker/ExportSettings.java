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

/**
 * class for holding export parameters and being the central point knowing which parameters to use
 * for certain apps
 */
class ExportSettings {

    /**
     * app name if intent shall be served with a specific app
     */
    private String appName;

    /**
     * waypoint file format to be used for export
     */
    private String format;

    /**
     * whether to use mime type in intent or not (false may help if app does not officially serve
     * a certain type)
     */
    private boolean useMimeType;

    /**
     * whether to use share intent instead of viewing intent
     */
    private boolean wantsToShare;

    /**
     * default constructor
     */
    ExportSettings() {
        initialize();
    }

    /**
     * constructor using presets for common apps
     *
     * @param appName app name to optimize the export for
     */
    ExportSettings(String appName) {
        switch (appName) {
            case "locus":
                initializeForLocus();
                break;
            case "cgeo":
                initializeForCGeo();
                break;
            default:
                initialize();
                break;
        }
    }

    /**
     * initialize with default settings, that should work for most apps
     */
    private void initialize() {
        appName = "";
        format = "gpx";
        useMimeType = true;
        wantsToShare = false;
    }

    /**
     * initialize with settings optimized for Locus Map
     */
    private void initializeForLocus() {
        appName = "locus";
        format = "kmz";
        useMimeType = true;
        wantsToShare = false;
    }

    /**
     * initialize with settings optimized for c:geo
     */
    private void initializeForCGeo() {
        appName = "cgeo";
        format = "gpx";
        useMimeType = false;
        wantsToShare = false;
    }

    /**
     * get app name
     *
     * @return app name
     */
    String getAppName() {
        return appName;
    }

    /**
     * set app name
     *
     * @param appName app name
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * get file format
     *
     * @return format
     */
    String getFormat() {
        return format;
    }

    /**
     * set file format
     *
     * @param format format
     */
    void setFormat(String format) {
        this.format = format;
    }

    /**
     * shall mime type be announced during export?
     *
     * @return true or false
     */
    boolean isUseMimeType() {
        return useMimeType;
    }

    /**
     * set if mime type shall be announced during export
     *
     * @param useMimeType true or false
     */
    void setUseMimeType(boolean useMimeType) {
        this.useMimeType = useMimeType;
    }

    /**
     * shall waypoints be shared rather than viewed?
     *
     * @return true or false
     */
    boolean isWantsToShare() {
        return wantsToShare;
    }

    /**
     * set whether waypoints shall be shared rather than viewed
     *
     * @param wantsToShare true or false
     */
    void setWantsToShare(boolean wantsToShare) {
        this.wantsToShare = wantsToShare;
    }
}
