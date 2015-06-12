package com.github.aesteve.vertx.nubes.exceptions;

public class BadRequestException extends HttpException {
    private static final long serialVersionUID = -8177274609736272204L;

    public BadRequestException(String message) {
        this(message, null);
    }
    
    public BadRequestException(String message, Throwable cause) {
    	super(400, message, cause);
    }
}
