package com.project.playlist.config;

import com.project.playlist.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.project.playlist.exceptions.ErrorCodes.*;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_PROCESSING_REQUEST_TEMPLATE = "Error processing request. Exception type: {}, Message: {}";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleUnknownException(Exception cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.GENERAL_ERROR).errorCode(INTERNAL_ERROR).status(status).path("").message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.BAD_REQUEST).errorCode(BAD_REQUEST).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleUserNotFoundException(HttpServletRequest request, UserNotFoundException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.NOT_FOUND).errorCode(NOT_FOUND).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleRoleNotFoundException(HttpServletRequest request, RoleNotFoundException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.NOT_FOUND).errorCode(NOT_FOUND).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handlePlaylistNotFoundException(HttpServletRequest request, PlaylistNotFoundException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.NOT_FOUND).errorCode(NOT_FOUND).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleVideoNotFoundException(HttpServletRequest request, VideoNotFoundException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.NOT_FOUND).errorCode(NOT_FOUND).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(DuplicateVideoUrlForUserException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleDuplicateVideoUrlForUserException(HttpServletRequest request, DuplicateVideoUrlForUserException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.CONFLICT;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.CONFLICT).errorCode(CONFLICT).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleUserAlreadyExistsException(HttpServletRequest request, UserAlreadyExistsException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.CONFLICT;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.CONFLICT).errorCode(CONFLICT).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

    @ExceptionHandler(VideoAlreadyInPlaylistException.class)
    @ResponseBody
    public HttpEntity<ErrorResponse> handleVideoAlreadyInPlaylistException(HttpServletRequest request, VideoAlreadyInPlaylistException cause) {
        log.error(ERROR_PROCESSING_REQUEST_TEMPLATE, cause.getClass(), cause.getMessage());

        HttpStatus status = HttpStatus.CONFLICT;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(cause.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.code();
        }
        return new ResponseEntity<>(ErrorResponse.builder().type(ErrorType.CONFLICT).errorCode(CONFLICT).status(status).path(request.getRequestURI()).message(cause.getMessage()).build(), status);
    }

}
