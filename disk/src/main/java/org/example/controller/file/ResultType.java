package org.example.controller.file;

import lombok.Data;

@Data
public  class ResultType<T> {
    private int code;
    private T data;
    private String message;

    public ResultType code(int code){
        this.code = code;
        return this;
    }
    public ResultType data(T data){
        this.data = data;
        return this;
    }
    public ResultType message(String message){
        this.message = message;
        return this;
    }
}
