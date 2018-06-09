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

    private String appName;
    private String format;
    private boolean useMimeType;
    private boolean wantsToShare;

    ExportSettings() {
        initialize();
    }

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

    private void initialize() {
        appName = null;
        format = "gpx";
        useMimeType = true;
        wantsToShare = false;
    }

    private void initializeForLocus() {
        appName = "locus";
        format = "kmz";
        useMimeType = true;
        wantsToShare = false;
    }

    private void initializeForCGeo() {
        appName = "cgeo";
        format = "gpx";
        useMimeType = false;
        wantsToShare = false;
    }

    String getAppName() {
        return appName;
    }

    @SuppressWarnings("unused")
    void setAppName(String appName) {
        this.appName = appName;
    }

    String getFormat() {
        return format;
    }

    void setFormat(String format) {
        this.format = format;
    }

    boolean isUseMimeType() {
        return useMimeType;
    }

    void setUseMimeType(boolean useMimeType) {
        this.useMimeType = useMimeType;
    }

    boolean isWantsToShare() {
        return wantsToShare;
    }

    void setWantsToShare(boolean wantsToShare) {
        this.wantsToShare = wantsToShare;
    }
}
