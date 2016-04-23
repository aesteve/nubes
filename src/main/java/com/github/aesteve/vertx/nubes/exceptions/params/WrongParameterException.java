package com.github.aesteve.vertx.nubes.exceptions.params;

public class WrongParameterException extends Exception {

	public enum ParamType {
		HEADER("header"),
		REQUEST_PARAM("request parameter");

		private final String name;

		ParamType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final long serialVersionUID = -2242939508622290913L;

	protected final String paramName;
	protected final ParamType type;

	public WrongParameterException(ParamType type, String paramName, Exception cause) {
		super(cause);
		this.type = type;
		this.paramName = paramName;
	}
}
