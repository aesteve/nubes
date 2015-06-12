package com.github.aesteve.vertx.nubes.exceptions;

public class MarshallingException extends Exception {

    private static final long serialVersionUID = 7662088198950308124L;

    public MarshallingException(Exception e) {
        super(e);
    }

}
