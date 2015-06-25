package com.github.aesteve.vertx.nubes.exceptions;

public class MissingConfigurationException extends Exception {

	private static final long serialVersionUID = 4138422360866737314L;

	public MissingConfigurationException(String node) {
		super("The " + node + " is missing in the configuration file, check conf.json");
	}
}
