package com.github.siggel.coordinatejoker;

/**
 * a named waypoint
 */
class Point {

    // point values
    private String name; // name
    private double latitude; // latitude in degrees
    private double longitude; // longitude in degrees

    /**
     * constructor
     *
     * @param name      name of the point
     * @param latitude  latitude value in degrees
     * @param longitude longitude value in degrees
     */
    Point(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // getters and setters need no explanation

    double getLatitude() {
        return latitude;
    }

    @SuppressWarnings("unused")
    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    double getLongitude() {
        return longitude;
    }

    @SuppressWarnings("unused")
    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    void setName(String name) {
        this.name = name;
    }
}
