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

import static junit.framework.TestCase.assertEquals;

public class CalculatorTest {

    @Test
    public void simple() {
        assertEquals(30.0, Calculator.evaluate("x+y", 10, 20));
    }

    @Test
    public void aLittleMoreComplex() {
        assertEquals(105.0, Calculator.evaluate("10*x + y/4", 10, 20));
    }

    @Test
    public void oneParenthesis() {
        assertEquals(30.0, Calculator.evaluate("(x + y)", 10, 20));
    }

    @Test
    public void substitutionParenthesis() {
        assertEquals(344.0, Calculator.evaluate("x(y/5)4", 3, 20));
    }

    @Test
    public void multipleParentheses() {
        assertEquals(324.0, Calculator.evaluate("(x+y)(x+x)(y+y)", 1, 2));
    }

    @Test
    public void multipleParenthesesWithProduct() {
        assertEquals(24.0, Calculator.evaluate("(x+y)*(x+x)*(y+y)", 1, 2));
    }

    @Test
    public void nestedParentheses() {
        assertEquals(42.0, Calculator.evaluate("(x+(x+y)) (x+x)", 1, 2));
        assertEquals(42.0, Calculator.evaluate("(x+(x+y))(x+x)", 1, 2));
    }

    @Test
    public void twoParenthesesGivingThreeDigits() {
        assertEquals(101.0, Calculator.evaluate("(10*x) (x)", 1, 0));
        assertEquals(101.0, Calculator.evaluate("(10*x)(x)", 1, 0));
    }

    @Test
    public void buildInFunction() {
        assertEquals(12.0, Calculator.evaluate("abs(-12)", 0, 0));
        assertEquals(12.0, Calculator.evaluate("abs(x)", -12, 0));
        assertEquals(11.0, Calculator.evaluate("abs(x+1)", -12, 0));
        assertEquals(10.0, Calculator.evaluate("abs(y(x+1))", -12, 1));
    }

    @Test
    public void allTogether() {
        assertEquals(45.0, Calculator.evaluate("(y-1) abs(-x)+1 x", 5, 7));
        assertEquals(45.0, Calculator.evaluate("(y-1)abs(-x)+1x", 5, 7));

        assertEquals(665.0, Calculator.evaluate("(y-1) (abs(-x)+1) (x)", 5, 7));
        assertEquals(665.0, Calculator.evaluate("(y-1)(abs(-x)+1)(x)", 5, 7));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSyntax() {
        Calculator.evaluate("x + ", 10, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unclosedParenthesis() {
        Calculator.evaluate("x(y/5", 10, 20);
    }
}