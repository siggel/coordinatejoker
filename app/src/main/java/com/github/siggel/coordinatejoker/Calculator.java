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

import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Class performing the calculations given string formulas and x value
 */
class Calculator {

    /**
     * constant for meter to feet conversion
     */
    final private static double meterPerFeet = 0.3048;

    /**
     * the formula mainModel
     */
    private final MainModel mainModel;


    /**
     * the app's main context (for accessing resource strings)
     */
    private final Context context;

    /**
     * constructor providing context and mainModel
     *
     * @param context   the main activities context
     * @param mainModel the formula mainModel
     */
    Calculator(Context context, MainModel mainModel) {
        this.context = context;
        this.mainModel = mainModel;
    }

    /**
     * method for calculating coordinates from the formulas given in the mainModel
     *
     * @return list of calculated waypoints
     */
    List<Point> solveX() {

        try {
            // initialize return list
            List<Point> list = new ArrayList<>();

            for (Integer x = mainModel.getXFrom(); x <= mainModel.getXTo(); ++x) {

                // get formulas from mainModel
                String degreesNorth = mainModel.getDegreesNorth();
                String degreesEast = mainModel.getDegreesEast();
                String minutesNorth = mainModel.getMinutesNorth();
                String minutesEast = mainModel.getMinutesEast();
                String distance = mainModel.getDistance();
                String azimuth = mainModel.getAzimuth();

                // for non-negative x do simple replacement so we support formulas like 12.34x,
                // but don't replace "exp" as it serves as exponential function
                if (x >= 0) {
                    final String xString = "" + x;
                    degreesNorth = degreesNorth.replaceAll("(?<!e)x(?!p)", xString);
                    degreesEast = degreesEast.replaceAll("(?<!e)x(?!p)", xString);
                    minutesNorth = minutesNorth.replaceAll("(?<!e)x(?!p)", xString);
                    minutesEast = minutesEast.replaceAll("(?<!e)x(?!p)", xString);
                    distance = distance.replaceAll("(?<!e)x(?!p)", xString);
                    azimuth = azimuth.replaceAll("(?<!e)x(?!p)", xString);
                }

                // first evaluate north and east coordinate
                double degrees;
                double minutes;
                degrees = new ExpressionBuilder(degreesNorth)
                        .variables("x")
                        .build()
                        .setVariable("x", 1.0 * x)
                        .evaluate();
                minutes = new ExpressionBuilder(minutesNorth)
                        .variables("x")
                        .build()
                        .setVariable("x", 1.0 * x)
                        .evaluate();
                if (invalidLatitudeDegrees(degrees) || invalidMinutes(minutes)) {
                    // skip invalid waypoints
                    continue;
                }
                double coordinateNorth =
                        (mainModel.getNorth() ? 1 : -1) * (degrees + (minutes / 60.0));

                degrees = new ExpressionBuilder(degreesEast)
                        .variables("x")
                        .build()
                        .setVariable("x", 1.0 * x)
                        .evaluate();
                minutes = new ExpressionBuilder(minutesEast)
                        .variables("x")
                        .build()
                        .setVariable("x", 1.0 * x)
                        .evaluate();
                if (invalidLongitudeDegrees(degrees) || invalidMinutes(minutes)) {
                    // skip invalid waypoints
                    continue;
                }
                double coordinateEast =
                        (mainModel.getEast() ? 1 : -1) * (degrees + (minutes / 60.0));

                // calculate projection delta
                double deltaDistance = new ExpressionBuilder(distance)
                        .variables("x")
                        .build()
                        .setVariable("x", 1.0 * x)
                        .evaluate();
                if (mainModel.getFeet())
                    deltaDistance *= meterPerFeet;

                double deltaAzimuth = new ExpressionBuilder(azimuth)
                        .variables("x")
                        .build()
                        .setVariable("x", 1.0 * x)
                        .evaluate();
                double deltaCoordinateNorth =
                        Math.cos(Math.toRadians(deltaAzimuth)) * deltaDistance;
                double deltaCoordinateEast =
                        Math.sin(Math.toRadians(deltaAzimuth)) * deltaDistance
                                / Math.cos(Math.toRadians(coordinateNorth));

                // add projection delta to coordinate
                coordinateNorth += deltaCoordinateNorth / 1850.0 / 60.0;
                coordinateEast += deltaCoordinateEast / 1850.0 / 60.0;

                // add waypoint to list
                Point point = new Point(
                        "x=" + String.valueOf(x),
                        coordinateNorth,
                        coordinateEast);
                list.add(point);
            }

            return list;

        } catch (Exception e) { // also catches RuntimeException
            throw new CalculatorException(context.getString(R.string.string_formula_error));
        }
    }

    /**
     * method checking if minutes are outside valid range
     *
     * @param minutes latitude or longitude minutes
     * @return true if minutes are outside range 0 <= minutes < 60
     */
    private boolean invalidMinutes(double minutes) {
        return minutes < 0 || minutes >= 60.0;
    }

    /**
     * method checking if latitude degrees are outside valid range
     *
     * @param degrees latitude degrees
     * @return true if degrees are outside range 0 <= minutes < 90
     */
    private boolean invalidLatitudeDegrees(double degrees) {
        return degrees < 0 || degrees >= 90.0;
    }

    /**
     * method checking if longitude degrees are outside valid range
     *
     * @param degrees longitude degrees
     * @return true if degrees are outside range 0 <= minutes < 90
     */
    private boolean invalidLongitudeDegrees(double degrees) {
        return degrees < 0 || degrees >= 180.0;
    }
}
