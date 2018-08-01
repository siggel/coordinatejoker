package com.github.siggel.coordinatejoker;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


public class CalculatorTest {

    @Test
    public void simple() {
        assertEquals(30.0,Calculator.evaluate("x+y", 10, 20));
    }

    @Test
    public void aLittleMoreComplex() {
        assertEquals(105.0,Calculator.evaluate("10*x + y/4", 10, 20));
    }

    @Test
    public void oneParenthesis() {
        assertEquals(30.0,Calculator.evaluate("(x + y)", 10, 20));
    }

    @Test
    public void substitutionParenthesis() {
        assertEquals(344.0,Calculator.evaluate("x(y/5)4", 3, 20));
    }

    @Test
    public void multipleParenthesis() {
        assertEquals(324.0,Calculator.evaluate("(x+y)(x+x)(y+y)", 1, 2));
    }

    @Test
    public void multipleParenthesisWithProduct() {
        assertEquals(24.0,Calculator.evaluate("(x+y)*(x+x)*(y+y)", 1, 2));
    }

    @Test
    public void nestedParenthesis() {
        assertEquals(42.0,Calculator.evaluate("(x+(x+y)) (x+x)", 1, 2));
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