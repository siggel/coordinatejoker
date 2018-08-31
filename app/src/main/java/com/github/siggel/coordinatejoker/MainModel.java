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

import java.util.List;

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
    private IntegerRange xValues = new IntegerRange(); // the x values
    private IntegerRange yValues = new IntegerRange(); // the y values

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
        xValues.setText("0-9");
        yValues.setText("");
    }

    /**
     * method for setting example values
     */
    void setExampleValues() {
        isNorth = true;
        degreesNorth = "53";
        minutesNorth = "11.6y6";
        isEast = true;
        degreesEast = "10";
        minutesEast = "(23400+20*x)/1000";
        distance = "100";
        isFeet = false;
        azimuth = "20*x";
        xValues.setText("2,4,5,6");
        yValues.setText("0-9@2");
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

    public List<Integer> getXValues() {
        return xValues.getValues();
    }

    public List<Integer> getYValues() {
        return yValues.getValues();
    }

    public void setXText(String text) {
        xValues.setText(text);
    }

    public String getXText() {
        return xValues.getText();
    }

    public void setYText(String text) {
        yValues.setText(text);
    }

    public String getYText() {
        return yValues.getText();
    }

}
