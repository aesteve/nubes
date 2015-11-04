package com.github.aesteve.vertx.nubes.exceptions.params;

public class InvalidParamValueException extends WrongParameterException {

	private static final long serialVersionUID = -8469141240265252774L;

	private Object paramValue;

	public InvalidParamValueException(ParamType type, String paramName, Object paramValue) {
		super(type, paramName);
		this.paramValue = paramValue;
	}

	public String getMessage() {
		if (type == null && paramName == null && paramValue == null) {
			return "Some request of form parameter has an invalid value";
		}
		return "Invalid value : " + paramValue + " for " + type.toString() + " : " + paramName;
	}

}
