package com.example.restfulMicroservices.author;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@Component
public class AppControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleContraintsViolationException(ConstraintViolationException ex){
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

        String errorMessage = constraintViolations.stream().map(e -> e.getMessage()).collect(Collectors.toList()).get(0);
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.BAD_REQUEST);

    }

    //NoAuthorCreatedException
    public ResponseEntity<?> handleNoAuthorCreatedException(NoAuthorCreatedException ex){
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

}
