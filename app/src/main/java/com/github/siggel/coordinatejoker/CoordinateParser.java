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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// intentionally one central definition of constants
@SuppressWarnings("FieldCanBeLocal")
class CoordinateParser {

    // constants
    private final String INVALID = "???"; // descriptive string indicating an invalid formula/range
    private final String targetLetters = "xy"; // variables x and y are supported
    private final String formulaPattern = "[\\w.\\s+\\-*/^()&&[^\\n]]*"; // alphanumeric.space+-*/^()
    private final String northDegreePattern = "([N,S])(" + formulaPattern + ")°?";
    private final String eastDegreePattern = "([E,W])(" + formulaPattern + ")°?";
    private final String minutesPattern = "(" + formulaPattern + ")'?";
    private final String distancePattern = "(" + formulaPattern + ")(m|ft)";
    private final String azimuthPattern = "(" + formulaPattern + ")°?\\s*TN";
    private final String spacePattern = "\\s*";
    private final String spaceOrCommaPattern = "[\\s,]*";
    private final MainModel model;
    private char letterToBeReplacedByStar;
    private String foundLetters;
    private boolean modelIsValid;

    CoordinateParser() {
        model = new MainModel();

        // set defaults
        model.setNorth(true);
        model.setDegreesNorth(INVALID);
        model.setMinutesNorth(INVALID);
        model.setEast(true);
        model.setDegreesEast(INVALID);
        model.setMinutesEast(INVALID);
        model.setDistance("0");
        model.setFeet(false);
        model.setAzimuth("0");
        model.setXRange(INVALID); // fill x with invalid value, so user cannot forget to adjust it
        model.setYRange("");

        letterToBeReplacedByStar = '*';
        foundLetters = "";
        modelIsValid = false;
    }

    private String orderLikeTargetLetters(String letters) {
        // if letters contains first target letter, bring it to the front
        letters = letters.replaceAll(
                "(.*)(" + targetLetters.charAt(0) + ")(.*)",
                "$2$1$3");
        // if letters contains second target letter, bring it to the end
        letters = letters.replaceAll(
                "(.*)(" + targetLetters.charAt(1) + ")(.*)",
                "$1$3$2");
        return letters;
    }

    private void tryToReplaceVariables() {
        // number of distinct letters in degrees' and minutes' formulas
        foundLetters = extractDistinctLetters(
                model.getDegreesNorth() + model.getMinutesNorth()
                        + model.getDegreesEast() + model.getMinutesEast()
                        + model.getDistance() + model.getAzimuth());

        // replace letters (variables) with x, y if no more than two letters were found
        if (foundLetters.length() <= 2) {

            // it is mandatory to order the found letters, so that we do not replace a>>x, then x>>y
            foundLetters = orderLikeTargetLetters(foundLetters);

            for (int i = 0; i < foundLetters.length(); ++i) {
                model.setDegreesNorth(
                        model.getDegreesNorth().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setMinutesNorth(
                        model.getMinutesNorth().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setDegreesEast(
                        model.getDegreesEast().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setMinutesEast(
                        model.getMinutesEast().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setDistance(
                        model.getDistance().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
                model.setAzimuth(
                        model.getAzimuth().replaceAll(String.valueOf(foundLetters.charAt(i)),
                                String.valueOf(targetLetters.charAt(i))));
            }
        }

        // fill y with invalid value if more than one variable is used, so user cannot forget it
        if (foundLetters.length() >= 2) {
            model.setYRange(INVALID);
        } else {
            model.setYRange("");
        }
    }

    private void parseString(String input) {
        final Pattern patternIncludingProjection = Pattern.compile(
                northDegreePattern + spacePattern + minutesPattern + spaceOrCommaPattern +
                        eastDegreePattern + spacePattern + minutesPattern + spaceOrCommaPattern +
                        distancePattern + spacePattern + azimuthPattern,
                Pattern.DOTALL);
        final Pattern patternWithoutProjection = Pattern.compile(
                northDegreePattern + spacePattern + minutesPattern + spaceOrCommaPattern +
                        eastDegreePattern + spacePattern + minutesPattern,
                Pattern.DOTALL);

        Matcher matcher = patternIncludingProjection.matcher(input);
        boolean matched = matcher.find();
        if (!matched) {
            // can we recognize coordinates without a projection?
            matcher = patternWithoutProjection.matcher(input);
            matched = matcher.find();
        }
        int matches = matched ? matcher.groupCount() : 0;

        // store extracted values in result model
        if (matches >= 6) {
            modelIsValid = true;

            // we found at least coordinates
            model.setNorth(Objects.requireNonNull(matcher.group(1)).trim().equals("N"));
            model.setDegreesNorth(Objects.requireNonNull(matcher.group(2)).trim());
            model.setMinutesNorth(Objects.requireNonNull(matcher.group(3)).trim());
            model.setEast(!Objects.requireNonNull(matcher.group(4)).trim().equals("W"));
            model.setDegreesEast(Objects.requireNonNull(matcher.group(5)).trim());
            model.setMinutesEast(Objects.requireNonNull(matcher.group(6)).trim());
            if (matches == 9) {
                // we also found projection data
                model.setDistance(Objects.requireNonNull(matcher.group(7)).trim());
                model.setFeet(Objects.requireNonNull(matcher.group(8)).trim().equals("ft"));
                model.setAzimuth(Objects.requireNonNull(matcher.group(9)).trim());
            } else {
                model.setDistance("0");
                model.setFeet(false);
                model.setAzimuth("0");
            }
        } else {
            modelIsValid = false;

            // mark invalid in result model
            fillModelWithInvalidValues();
        }

    }

    private void fillModelWithInvalidValues() {
        model.setNorth(true);
        model.setDegreesNorth(INVALID);
        model.setMinutesNorth(INVALID);
        model.setEast(true);
        model.setDoReplaceMinutes(true);
        model.setDegreesEast(INVALID);
        model.setMinutesEast(INVALID);
        model.setDistance(INVALID);
        model.setAzimuth(INVALID);
    }

    private String preprocessMultiplicationOperator(String input) {
        if (letterToBeReplacedByStar != '*') {
            input = input.replace(String.valueOf(letterToBeReplacedByStar), "*");
        }
        return input;
    }

    private String preprocessSubtractionOperator(String input) {
        // replace long minus by normal minus
        input = input.replace("−", "-");
        return input;
    }

    private String preprocessBrackets(String input) {
        input = input.replace("[", "(");
        input = input.replace("]", ")");
        return input;
    }

    private String extractDistinctLetters(String input) {
        // remove all but letters (variables and function names remain)
        String cleanedInput = input.replaceAll("[^A-Za-z]", "");
        StringBuilder extractedLetters = new StringBuilder();
        for (int i = 0; i < cleanedInput.length(); ++i) {
            if (!extractedLetters.toString().contains(String.valueOf(cleanedInput.charAt(i)))) {
                extractedLetters.append(cleanedInput.charAt(i));
            }
        }
        return extractedLetters.toString();
    }

    void processInput(String input) {
        String result = input;
        result = preprocessMultiplicationOperator(result);
        result = preprocessSubtractionOperator(result);
        result = preprocessBrackets(result);
        parseString(result);
        tryToReplaceVariables();
    }

    void setLetterToBeReplacedByStar(char letterToBeReplacedByStar) {
        this.letterToBeReplacedByStar = letterToBeReplacedByStar;
    }

    String getReplacementInfo() {
        String variableReplacementInfo = "";
        if (modelIsValid) {
            if (foundLetters.length() <= 2) {
                if (!foundLetters.isEmpty()) {
                    variableReplacementInfo = foundLetters.charAt(0) + " >> "
                            + targetLetters.charAt(0);
                }
                if (foundLetters.length() == 2) {
                    variableReplacementInfo += ", ";
                    variableReplacementInfo += foundLetters.charAt(1) + " >> "
                            + targetLetters.charAt(1);
                }
            }
        }
        return variableReplacementInfo;
    }

    boolean isModelValid() {
        return modelIsValid;
    }

    MainModel getModel() {
        return model;
    }

}