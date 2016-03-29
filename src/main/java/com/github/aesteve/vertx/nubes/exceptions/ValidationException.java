package com.github.aesteve.vertx.nubes.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {

	private static final long serialVersionUID = 7605372874231630986L;

	private final List<String> validationMsgs;

	private ValidationException() {
		super();
		validationMsgs = new ArrayList<>();
	}
	
	public ValidationException(String msg) {
		super(msg);
		validationMsgs = new ArrayList<>();
		validationMsgs.add(msg);
	}
	
	public ValidationException(List<String> msgs) {
		this();
		validationMsgs.addAll(msgs);
	}
	public String getValidationMsg() {
		StringBuilder sb = new StringBuilder("Invalid data.");
		validationMsgs.forEach(msg -> sb.append(msg).append(".\n"));
		return sb.toString();
	}
}
