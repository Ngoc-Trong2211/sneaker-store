package com.example.sneaker_store.util.exception;

import com.example.sneaker_store.model.response.SystemResponse;
import com.example.sneaker_store.util.exception.User.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalHandleException {

    @ExceptionHandler(value = {
            EmailExistsAlreadyException.class,
            EmailInvalidException.class,
            IdInvalidException.class,
            PhoneExistsAlreadyException.class,
            PasswordMismatchException.class,
            ChangePasswordException.class
    })
    public ResponseEntity<SystemResponse<Object>> handleExceptionForUser(Exception ex){
        SystemResponse<Object> res = new SystemResponse<>();
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setData(null);
        res.setMessage("Error in system =>>>>> " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SystemResponse<Object>> handleExceptionMethodArgument(MethodArgumentNotValidException ex){
        SystemResponse<Object> res = new SystemResponse<>();
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setData(null);
        System.out.println(ex.getAllErrors().get(0).getDefaultMessage());
        List<String> messages = ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        res.setMessage(messages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
