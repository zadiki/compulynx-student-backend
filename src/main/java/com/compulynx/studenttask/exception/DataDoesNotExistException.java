package com.compulynx.studenttask.exception;

public class DataDoesNotExistException extends RuntimeException{

    public DataDoesNotExistException(String message){
        super(message);
    }
}
