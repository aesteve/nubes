package com.github.aesteve.vertx.nubes.exceptions.params;

public class WrongParameterException extends Exception {
	
	public static enum ParamType {
		HEADER("header"),
		REQUEST_PARAM("request parameter");
		
		private String name;
		
		private ParamType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private static final long serialVersionUID = -2242939508622290913L;
	
	protected String paramName;
	protected ParamType type;
	
	public WrongParameterException(ParamType type, String paramName) {
		super();
		this.type = type;
		this.paramName = paramName;
	}
}
