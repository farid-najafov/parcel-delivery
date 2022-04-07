package com.delivery.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.delivery.model.shared.ErrorResponse;
import io.micrometer.core.lang.NonNullApi;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@NonNullApi
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String VALIDATION_FAILED_MSG = "Validation failed!";
    public static final String UNSUPPORTED_METHOD_MSG = "This http method is not allowed here!";
    public static final String INTERNAL_ERROR_MSG = "Request cannot be processed by the server!";
    public static final String RECORD_NOT_FOUND_MSG = "Record not found!";
    public static final String PARAM_TYPE_MISMATCH_MSG = "Parameter type is incorrect!";
    public static final String MISSING_REQUEST_HEADER_MSG = "Request header is missing";
    public static final String ADDRESS_CHANGE_NOT_ALLOWED_MSG = "Address change not allowed";

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, ex.getMostSpecificCause().getMessage(), ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(BAD_REQUEST, PARAM_TYPE_MISMATCH_MSG, ex.getMostSpecificCause().getMessage(), ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorDetail = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, errorDetail, ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(METHOD_NOT_ALLOWED, UNSUPPORTED_METHOD_MSG, ex.getMessage(), ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, ex.getLocalizedMessage(), ex, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        String errorDetail = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, errorDetail, ex, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleHibernateConstraintViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        String errorDetail = ex.getLocalizedMessage();
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, errorDetail, ex, request);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    protected ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
        String errorDetail = ex.getLocalizedMessage();
        return getError(NOT_FOUND, RECORD_NOT_FOUND_MSG, errorDetail, ex, request);
    }

    @ExceptionHandler(AddressChangeNotAllowedException.class)
    protected ResponseEntity<Object> handleAddressChangeNotAllowedException(
            AddressChangeNotAllowedException ex, WebRequest request) {
        String errorDetail = ex.getLocalizedMessage();
        return getError(BAD_REQUEST, ADDRESS_CHANGE_NOT_ALLOWED_MSG, errorDetail, ex, request);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<Object> handleMissingRequestHeaderException(
            MissingRequestHeaderException ex, WebRequest request) {
        String errorDetail = ex.getLocalizedMessage();
        return getError(BAD_REQUEST, MISSING_REQUEST_HEADER_MSG, errorDetail, ex, request);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<Object> handleValidationException(
            ValidationException ex, WebRequest request) {
        String errorDetail = ex.getLocalizedMessage();
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, errorDetail, ex, request);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        String errorDetail = ex.getMessage();
        return getError(INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MSG, errorDetail, ex, request);
    }

    private ResponseEntity<Object> getError(
            HttpStatus status, String message, String errorDetail, Exception ex, WebRequest request) {
        log.error("{} : {}", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .errorDetail(errorDetail)
                        .path(request.getDescription(false))
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
