package com.github.siggel.coordinatejoker;

/**
 * Exception thrown when export goes wrong
 */
class ExportException extends RuntimeException {

    /**
     * constructor
     *
     * @param message exception's message
     */
    ExportException(String message) {
        super(message);
    }
}
