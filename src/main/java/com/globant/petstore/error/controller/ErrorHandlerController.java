package com.globant.petstore.error.controller;

import com.globant.petstore.command.exception.ResourceAlreadyExistsException;
import com.globant.petstore.command.exception.ResourceConflictException;
import com.globant.petstore.command.exception.ResourceNotFoundException;
import com.globant.petstore.response.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandlerController {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        ErrorMessage
                                .builder()
                                .withMessage("Resource already exists.")
                                .build());
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorMessage> handleResourceConflictException(ResourceConflictException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        ErrorMessage
                                .builder()
                                .withMessage("Resource conflict, indicated request version is not the latest.")
                                .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorMessage
                                .builder()
                                .withMessage("Resource does not exist.")
                                .build());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorMessage> handleRequestBeanValidationException(WebExchangeBindException exception) {

        final String errorMessage =
                exception.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .sorted()
                        .collect(Collectors.joining(", "));
        log.error(errorMessage, exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorMessage
                                .builder()
                                .withMessage(errorMessage)
                                .build());
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorMessage> handleRequestBeanValidationException(ServerWebInputException exception) {

        final String errorMessage =
                Optional.ofNullable(exception.getReason())
                        .orElse("Some mandatory fields were not included in the request.");
        log.error(errorMessage, exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorMessage
                                .builder()
                                .withMessage(errorMessage)
                                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        ErrorMessage
                                .builder()
                                .withMessage("Resource already exists.")
                                .build());
    }
}
