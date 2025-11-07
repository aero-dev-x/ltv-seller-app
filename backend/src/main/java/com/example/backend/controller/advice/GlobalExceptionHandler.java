package com.example.backend.controller.advice;

import com.example.backend.dto.ErrorResponse;
import com.example.backend.exception.SellerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(SellerNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleSellerNotFound(
			SellerNotFoundException ex, WebRequest request) {
		logger.warn("Seller not found: {}", ex.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			"Not Found",
			ex.getMessage()
		);
		error.setPath(getRequestPath(request));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(
			IllegalArgumentException ex, WebRequest request) {
		logger.warn("Invalid argument: {}", ex.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			ex.getMessage()
		);
		error.setPath(getRequestPath(request));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
	public ResponseEntity<ErrorResponse> handleValidationException(
			Exception ex, WebRequest request) {
		logger.warn("Validation error: {}", ex.getMessage());
		
		String message = "Validation failed: " + ex.getMessage();
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			message
		);
		error.setPath(getRequestPath(request));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception ex, WebRequest request) {
		logger.error("Unexpected error occurred", ex);
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			"Internal Server Error",
			"An unexpected error occurred while processing your request"
		);
		error.setPath(getRequestPath(request));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	private String getRequestPath(WebRequest request) {
		String description = request.getDescription(false);
		return description != null ? description.replace("uri=", "") : "";
	}
}
