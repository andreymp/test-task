package org.fieldwire.challenge.exception;


import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

	private final ErrorType type;
	private final String message;

	public ServiceException(ErrorType type, String message) {
		super(message);
		this.type = type;
		this.message = message;
	}
}
