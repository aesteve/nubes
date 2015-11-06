package com.github.aesteve.vertx.nubes.exceptions.http.impl;

import com.github.aesteve.vertx.nubes.exceptions.http.HttpException;

public class ForbiddenException extends HttpException {

	private static final long serialVersionUID = 2599248274879711072L;

	public ForbiddenException() {
		super(403, "Forbidden");
	}

	public ForbiddenException(String additionalMsg) {
		super(403, "Forbidden: " + additionalMsg);
	}

}
