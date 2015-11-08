package com.github.aesteve.vertx.nubes.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {
	
	private static final long serialVersionUID = 7605372874231630986L;
	
	private List<String> validationMsgs;
	
	public ValidationException(String msg) {
		super(msg);
		validationMsgs = new ArrayList<>();
		validationMsgs.add(msg);
	}
	
	public String getValidationMsg() {
		StringBuilder sb = new StringBuilder("Invalid data.");
		validationMsgs.forEach(msg -> sb.append(msg + ".\n"));
		return sb.toString();
	}
}
