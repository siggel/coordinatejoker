package com.github.siggel.coordinatejoker;

/**
 * Exception thrown when solving formulas goes wrong
 */
class CalculatorException extends RuntimeException {

    /**
     * constructor
     *
     * @param message exception's message
     */
    CalculatorException(String message) {
        super(message);
    }
}
