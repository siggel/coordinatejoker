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
        distance = "";
        isFeet = false;
        azimuth = "";
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
