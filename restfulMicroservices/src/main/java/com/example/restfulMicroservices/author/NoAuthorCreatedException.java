package com.example.restfulMicroservices.author;

public class NoAuthorCreatedException extends RuntimeException{

    public NoAuthorCreatedException(String message){
        super(message);
    }
}
