package com.project.playlist.config;

import com.project.playlist.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_PROCESSING_REQUEST_TEMPLATE = "Error processing request. Exception type: {}, Message: {}";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleUnknownException(Exception cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({UserNotFoundException.class, PlaylistNotFoundException.class, VideoNotFoundException.class})
    @ResponseBody
    public HttpEntity<ErrorResponse> handleEntityNotFoundException(UserNotFoundException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateVideoUrlForUserException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleDuplicateVideoUrlForUserException(DuplicateVideoUrlForUserException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
