package com.example.shoppingweb.exception;

import com.example.shoppingweb.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ShopExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseDTO<String> globalExceptionHandler(Exception e) {

        return new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}
