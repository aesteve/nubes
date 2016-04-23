package com.github.aesteve.vertx.nubes.exceptions.http.impl;

import com.github.aesteve.vertx.nubes.exceptions.http.HttpException;

public class UnauthorizedException extends HttpException {

  private static final long serialVersionUID = 2599248274879711072L;

  public UnauthorizedException() {
    super(401, "Unauthorized");
  }
}
