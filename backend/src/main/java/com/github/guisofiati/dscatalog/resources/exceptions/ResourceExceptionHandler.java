package com.github.guisofiati.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.guisofiati.dscatalog.services.exceptions.EntityNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler {
	
	// vai interceptar o controller e tratar a exception
	@ExceptionHandler(EntityNotFoundException.class) //toda vez que estourar essa exception, vai ser tratado por esse metodo
	public ResponseEntity<StandardError> entityNotFound(EntityNotFoundException e, HttpServletRequest req) {
		StandardError err = new StandardError();
		int status = HttpStatus.NOT_FOUND.value();
		err.setTimestamp(Instant.now());
		err.setStatus(status);
		err.setError("Resource not found");
		err.setMessage(e.getMessage());
		err.setPath(req.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
}
