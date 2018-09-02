/*
 * Copyright (c) 2018 by bubendorf <markus@bubendorf.ch>
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

import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntegerRangeTest {

    @Test
    public void empty() {
        List<Integer> values = IntegerRange.getValues(null, " ");
        assertEquals(0, values.size());
    }

    @Test
    public void singleNumber() {
        List<Integer> values = IntegerRange.getValues(null, "42");
        assertEquals(1, values.size());
        assertEquals(42, values.get(0).intValue());
    }

    @Test
    public void multipleNumbers() {
        List<Integer> values = IntegerRange.getValues(null, "42,13,45");
        assertEquals(3, values.size());
        assertEquals(13, values.get(0).intValue());
        assertEquals(42, values.get(1).intValue());
        assertEquals(45, values.get(2).intValue());
    }

    @Test
    public void range() {
        List<Integer> values = IntegerRange.getValues(null, "13-15");
        assertEquals(3, values.size());
        assertEquals(13, values.get(0).intValue());
        assertEquals(14, values.get(1).intValue());
        assertEquals(15, values.get(2).intValue());
    }

    @Test
    public void rangeWithStep() {
        List<Integer> values = IntegerRange.getValues(null, "2-8#2");
        assertEquals(4, values.size());
        assertEquals(2, values.get(0).intValue());
        assertEquals(4, values.get(1).intValue());
        assertEquals(6, values.get(2).intValue());
        assertEquals(8, values.get(3).intValue());
    }

    @Test
    public void complex() {
        List<Integer> values = IntegerRange.getValues(null, "0,2-4,7,10-20#5,33");
        assertEquals(9, values.size());
        assertEquals(0, values.get(0).intValue());
        assertEquals(2, values.get(1).intValue());
        assertEquals(3, values.get(2).intValue());
        assertEquals(4, values.get(3).intValue());
        assertEquals(7, values.get(4).intValue());
        assertEquals(10, values.get(5).intValue());
        assertEquals(15, values.get(6).intValue());
        assertEquals(20, values.get(7).intValue());
        assertEquals(33, values.get(8).intValue());
    }

    @Test
    public void complexWithSpaces() {
        List<Integer> values = IntegerRange.getValues(null, " 0 , 2 - 4, 7 , 10 - 20 # 5 , 33");
        assertEquals(9, values.size());
        assertEquals(0, values.get(0).intValue());
        assertEquals(2, values.get(1).intValue());
        assertEquals(3, values.get(2).intValue());
        assertEquals(4, values.get(3).intValue());
        assertEquals(7, values.get(4).intValue());
        assertEquals(10, values.get(5).intValue());
        assertEquals(15, values.get(6).intValue());
        assertEquals(20, values.get(7).intValue());
        assertEquals(33, values.get(8).intValue());
    }

    @Test(expected = ParseException.class)
    public void noNegativeNumbers() {
        IntegerRange.getValues(null, "-42");
    }

    @Test(expected = ParseException.class)
    public void fromIsHigherThanTo() {
        IntegerRange.getValues(null, "30-20");
    }

    @Test(expected = ParseException.class)
    public void negativeStep() {
        IntegerRange.getValues(null, "10-20#-5");
    }

    @Test(expected = ParseException.class)
    public void noDigits() {
        IntegerRange.getValues(null, "12,abc");
    }

}


