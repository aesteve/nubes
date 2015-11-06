package com.github.aesteve.vertx.nubes.exceptions.http.impl;

import com.github.aesteve.vertx.nubes.exceptions.http.HttpException;

public class BadRequestException extends HttpException {

	private static final long serialVersionUID = -8255219554555226446L;

	public BadRequestException() {
		super(400, "Bad request");
	}

	public BadRequestException(String msg) {
		super(400, "Bad request. " + msg);
	}
}
