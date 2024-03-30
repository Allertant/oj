package com.shiyixi.ojbackendemailservice.exception;

public class EmailTypeNotFoundException extends RuntimeException{
    public EmailTypeNotFoundException(String message) {
        super(message);
    }
}
