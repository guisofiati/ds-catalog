package com.github.guisofiati.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.guisofiati.dscatalog.services.exceptions.DatabaseException;
import com.github.guisofiati.dscatalog.services.exceptions.QueryParameterException;
import com.github.guisofiati.dscatalog.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler {
	
	// vai interceptar o controller e tratar a exception
	@ExceptionHandler(ResourceNotFoundException.class) //toda vez que estourar essa exception, vai ser tratado por esse metodo
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest req) {
		StandardError err = new StandardError();
		HttpStatus status = HttpStatus.NOT_FOUND;
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Resource not found");
		err.setMessage(e.getMessage());
		err.setPath(req.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(DatabaseException.class) //toda vez que estourar essa exception, vai ser tratado por esse metodo
	public ResponseEntity<StandardError> databaseException(DatabaseException e, HttpServletRequest req) {
		StandardError err = new StandardError();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Database exception");
		err.setMessage(e.getMessage());
		err.setPath(req.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(QueryParameterException.class) //toda vez que estourar essa exception, vai ser tratado por esse metodo
	public ResponseEntity<StandardError> queryParamException(QueryParameterException e, HttpServletRequest req) {
		StandardError err = new StandardError();
		HttpStatus status = HttpStatus.NOT_FOUND;
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Query parameter not found");
		err.setMessage(e.getMessage());
		err.setPath(req.getRequestURI() + "/" + req.getQueryString());
		return ResponseEntity.status(status).body(err);
	}
}
