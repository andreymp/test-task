package org.fieldwire.challenge.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
	private ErrorType type;
	private String message;
}
