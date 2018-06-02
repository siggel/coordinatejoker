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
 * class containing formulas of the main gui
 */
class MainModel {

    private Boolean isNorth; // false means south
    private String degreesNorth; // string representation of degrees
    private String minutesNorth; // string representation of minutes
    private Boolean isEast; // false means west
    private String degreesEast; // string representation of degrees
    private String minutesEast; // string representation of minutes
    private String distance; // in meter or feet
    private Boolean isFeet; // distance given in feet instead of meter
    private String azimuth; // in degrees
    private Integer xFrom; // string representation of variable x start value
    private Integer xTo; // string representation of variable x end value

    /**
     * constructor
     */
    MainModel() {
        reset();
    }

    /**
     * method for resetting model values
     */
    void reset() {
        isNorth = true;
        degreesNorth = "";
        minutesNorth = "";
        isEast = true;
        degreesEast = "";
        minutesEast = "";
        distance = "0";
        isFeet = false;
        azimuth = "0";
        xFrom = 0;
        xTo = 9;
    }

    /**
     * method for setting example values
     */
    void setExampleValues() {
        isNorth = true;
        degreesNorth = "53";
        minutesNorth = "11.660";
        isEast = true;
        degreesEast = "10";
        minutesEast = "(23400+20*x)/1000";
        distance = "100";
        isFeet = false;
        azimuth = "20*x";
        xFrom = 0;
        xTo = 9;
    }


    // the getters and setters need no explanation
    Boolean getNorth() {
        return isNorth;
    }

    void setNorth(Boolean north) {
        isNorth = north;
    }

    String getDegreesNorth() {
        return degreesNorth;
    }

    void setDegreesNorth(String degreesNorth) {
        this.degreesNorth = degreesNorth;
    }

    String getMinutesNorth() {
        return minutesNorth;
    }

    void setMinutesNorth(String minutesNorth) {
        this.minutesNorth = minutesNorth;
    }

    Boolean getEast() {
        return isEast;
    }

    void setEast(Boolean east) {
        isEast = east;
    }

    String getDegreesEast() {
        return degreesEast;
    }

    void setDegreesEast(String degreesEast) {
        this.degreesEast = degreesEast;
    }

    String getMinutesEast() {
        return minutesEast;
    }

    void setMinutesEast(String minutesEast) {
        this.minutesEast = minutesEast;
    }

    String getDistance() {
        return distance;
    }

    void setDistance(String distance) {
        this.distance = distance;
    }

    Boolean getFeet() {
        return isFeet;
    }

    void setFeet(Boolean feet) {
        isFeet = feet;
    }

    String getAzimuth() {
        return azimuth;
    }

    void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    Integer getXFrom() {
        return xFrom;
    }

    void setXFrom(Integer xFrom) {
        this.xFrom = xFrom;
    }

    Integer getXTo() {
        return xTo;
    }

    void setXTo(Integer xTo) {
        this.xTo = xTo;
    }
}
