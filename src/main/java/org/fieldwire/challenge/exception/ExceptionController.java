package org.fieldwire.challenge.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@ControllerAdvice(annotations = {RestController.class})
public class ExceptionController implements ErrorController {

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorMessage> handleException(Exception e) {
		log.error(e.getMessage());
		ErrorMessage errorMessage = new ErrorMessage()
				.setType(ErrorType.INTERNAL_SERVER_ERROR)
				.setMessage(e.getMessage());
		return toResponseEntity(errorMessage);
	}

	@ExceptionHandler({ServiceException.class})
	public ResponseEntity<ErrorMessage> handleServiceException(ServiceException e) {
		log.error(e.getMessage());
		ErrorMessage errorMessage = new ErrorMessage()
				.setType(e.getType())
				.setMessage(e.getMessage());
		return toResponseEntity(errorMessage);
	}

	private ResponseEntity<ErrorMessage> toResponseEntity(ErrorMessage errorMessage) {
		int status = Optional.ofNullable(errorMessage.getType())
						.map(ErrorType::getStatus)
						.orElse(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return ResponseEntity.status(status).body(errorMessage);
	}
}
