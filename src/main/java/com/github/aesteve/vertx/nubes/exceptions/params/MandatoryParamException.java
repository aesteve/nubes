package com.github.aesteve.vertx.nubes.exceptions.params;

public class MandatoryParamException extends WrongParameterException {

	private static final long serialVersionUID = -4570023655611653765L;

	public MandatoryParamException(ParamType type, String paramName) {
		super(type, paramName);
	}

	@Override
	public String getMessage() {
		return type.toString() + " : " + paramName + " is mandatory";
	}
}
