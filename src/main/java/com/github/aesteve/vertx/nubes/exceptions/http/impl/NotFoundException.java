package com.github.aesteve.vertx.nubes.exceptions.http.impl;

import com.github.aesteve.vertx.nubes.exceptions.http.HttpException;

public class NotFoundException extends HttpException {

	private static final long serialVersionUID = -4393223169076877568L;

	public NotFoundException() {
		super(404, "Not found");
	}

	public NotFoundException(String resourceName) {
		super(404, "Not found: " + resourceName);
	}

}
