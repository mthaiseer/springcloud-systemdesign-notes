package com.example.restfulMicroservices.author;

public class ErrorMessage {

    String errorMessage;
    public ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
