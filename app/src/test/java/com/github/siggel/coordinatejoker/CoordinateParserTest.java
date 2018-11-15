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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CoordinateParserTest {

    private static CoordinateParser coordinateParser = new CoordinateParser();

    @SuppressWarnings("MethodWithTooManyParameters")
    private static void assertModelValues(MainModel model,
                                          boolean isNorth, String degreesNorth, String minutesNorth,
                                          boolean isEast, String degreesEast, String minutesEast,
                                          String distance, boolean isFeet, String azimuth) {
        assertEquals(isNorth, model.getNorth());
        assertEquals(degreesNorth, model.getDegreesNorth());
        assertEquals(minutesNorth, model.getMinutesNorth());
        assertEquals(isEast, model.getEast());
        assertEquals(degreesEast, model.getDegreesEast());
        assertEquals(minutesEast, model.getMinutesEast());
        assertEquals(distance, model.getDistance());
        assertEquals(isFeet, model.getFeet());
        assertEquals(azimuth, model.getAzimuth());
    }

    @Test
    public void simpleParsing() {
        String input = "N 12° 34.567' W 89° 01.234' 5678m 90°TN";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                true, "12", "34.567",
                false, "89", "01.234",
                "5678", false, "90");
        assertEquals("", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void parsingWithFormulaFields() {
        String input = "S 12*(x)° 34/x.(567)' E (8+9)° 01.2(3-x)4' 5(6^x)78ft 9(0)°TN";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                false, "12*(x)", "34/x.(567)",
                true, "(8+9)", "01.2(3-x)4",
                "5(6^x)78", true, "9(0)");
        assertEquals("x >> x", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void parsingWithoutProjection() {
        String input = "S 12*(x)° 34/x.(567)' E (8+9)° 01.2(3-x)4'abcdef";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                false, "12*(x)", "34/x.(567)",
                true, "(8+9)", "01.2(3-x)4",
                "0", false, "0");
        assertEquals("x >> x", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void parsingWithNoMinuteSigns() {
        String input = "S 12*(x)° 34/x.(567) E (8+9)° 01.2(3-x)4";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                false, "12*(x)", "34/x.(567)",
                true, "(8+9)", "01.2(3-x)4",
                "0", false, "0");
        assertEquals("x >> x", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void parsingWithNewlines() {
        String input = "S 12*(x)\n 34/x.(567) \nE (8+9)\n 01.2(3-x)4 \n5(6^x)78ft \n 9(0)TN";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                false, "12*(x)", "34/x.(567)",
                true, "(8+9)", "01.2(3-x)4",
                "5(6^x)78", true, "9(0)");
        assertEquals("x >> x", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void invalidInput() {
        String input = "12° 34.567' W 89° 01.234' 5678m 90°TN";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                true, "???", "???",
                true, "???", "???",
                "???", false, "???");
        assertEquals("", coordinateParser.getReplacementInfo());
        assertFalse(coordinateParser.isModelValid());
    }

    @Test
    public void parseWithXForMultiplication() {
        String input = "S 12*(x)° 34/x.(567) E (8+9)° 01.2(3-x)4' 5(6^x)78m 9(0)°TN";
        coordinateParser.setLetterToBeReplacedByStar('x');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                false, "12*(*)", "34/*.(567)",
                true, "(8+9)", "01.2(3-*)4",
                "5(6^*)78", false, "9(0)");
        assertEquals("", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void parseWithDiagonalCrossForMultiplication() {
        String input = "S 12*(×)° 34/×.(567) E (8+9)° 01.2(3-×)4' 5(6^×)78m 9(0)°TN";
        coordinateParser.setLetterToBeReplacedByStar('×');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                false, "12*(*)", "34/*.(567)",
                true, "(8+9)", "01.2(3-*)4",
                "5(6^*)78", false, "9(0)");
        assertEquals("", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void simpleReplacementOfOneVariableWithX() {
        String input = "N 12° 34.567' W 89° 01.234' 56a8m 90°TN";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                true, "12", "34.567",
                false, "89", "01.234",
                "56x8", false, "90");
        assertEquals("a >> x", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void simpleReplacementOfTwoVariablesWithXYInOrderOfAppearance() {
        String input = "N 12° 34.5b7' W 89° 01.234' 56a8m 90°TN";
        coordinateParser.setLetterToBeReplacedByStar('*');
        coordinateParser.processInput(input);
        assertModelValues(coordinateParser.getModel(),
                true, "12", "34.5x7",
                false, "89", "01.234",
                "56y8", false, "90");
        assertEquals("b >> x, a >> y", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void specialReplacementXStaysX() {
        String input = "N 12° 34.5b7' W 89° 01.234' 56x8m 90°TN";
        coordinateParser.processInput(input);
        coordinateParser.setLetterToBeReplacedByStar('*');
        assertModelValues(coordinateParser.getModel(),
                true, "12", "34.5y7",
                false, "89", "01.234",
                "56x8", false, "90");
        assertEquals("x >> x, b >> y", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void specialReplacementYStaysY() {
        String input = "N 12° 34.5y7' W 89° 01.234' 56a8m 90°TN";
        coordinateParser.processInput(input);
        coordinateParser.setLetterToBeReplacedByStar('*');
        assertModelValues(coordinateParser.getModel(),
                true, "12", "34.5y7",
                false, "89", "01.234",
                "56x8", false, "90");
        assertEquals("a >> x, y >> y", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void specialReplacementYXStaysYXIfYOccursFirst() {
        String input = "N 12° 34.5y7' W 89° 01.234' 56x8m 90°TN";
        coordinateParser.processInput(input);
        coordinateParser.setLetterToBeReplacedByStar('*');
        assertModelValues(coordinateParser.getModel(),
                true, "12", "34.5y7",
                false, "89", "01.234",
                "56x8", false, "90");
        assertEquals("x >> x, y >> y", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }

    @Test
    public void noReplacement() {
        String input = "N 12° 34.5y7' W 89° 01.234' 56x8m 9z°TN";
        coordinateParser.processInput(input);
        coordinateParser.setLetterToBeReplacedByStar('*');
        assertModelValues(coordinateParser.getModel(),
                true, "12", "34.5y7",
                false, "89", "01.234",
                "56x8", false, "9z");
        assertEquals("", coordinateParser.getReplacementInfo());
        assertTrue(coordinateParser.isModelValid());
    }
}
