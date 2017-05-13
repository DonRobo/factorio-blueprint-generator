package com.donrobo.fpbg.parser;

public class ParsingError extends RuntimeException {

    public ParsingError() {
    }

    public ParsingError(String message) {
        super(message);
    }

    public ParsingError(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingError(Throwable cause) {
        super(cause);
    }

    public ParsingError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
