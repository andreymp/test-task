package org.fieldwire.challenge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorType {

	BAD_REQUEST(HttpStatus.BAD_REQUEST.value()),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value()),
	NOT_FOUND(HttpStatus.NOT_FOUND.value());

	@Getter
	private final int status;

	ErrorType(int status) {
		this.status = status;
	}
}
