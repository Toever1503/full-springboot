package com.webs.exception;


import com.dtos.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto inValidArguments(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        Map<Object, String> errors = new HashMap<>();
        //collect errors
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseDto("Miss parameters", "ERROR", errors);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseDto inValidArguments(RuntimeException ex) {
        ex.printStackTrace();
        return new ResponseDto(ex.getMessage(), "ERROR", ex.getMessage());
    }
}
