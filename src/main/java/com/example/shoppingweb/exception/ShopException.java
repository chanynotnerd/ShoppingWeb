package com.example.shoppingweb.exception;

public class ShopException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ShopException(String message) {
        super(message);
    }
}
