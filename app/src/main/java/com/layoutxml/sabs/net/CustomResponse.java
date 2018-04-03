package com.layoutxml.sabs.net;


public class CustomResponse<T> {
    public int error;
    public String message;
    public T data;

    public CustomResponse() {
    }

    public CustomResponse(int error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
    }
}
