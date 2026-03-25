package com.example.sneaker_store.util.exception;

import com.example.sneaker_store.model.response.SystemResponse;
import com.example.sneaker_store.util.exception.User.EmailExistsAlreadyException;
import com.example.sneaker_store.util.exception.User.EmailInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandleException {

    @ExceptionHandler(value = {
            EmailExistsAlreadyException.class,
            EmailInvalidException.class
    })
    public ResponseEntity<SystemResponse<Object>> handleExceptionForUser(Exception ex){
        SystemResponse<Object> res = new SystemResponse<>();
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setData(null);
        res.setMessage("Error in system =>>>>> " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
