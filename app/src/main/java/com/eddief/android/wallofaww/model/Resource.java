package com.eddief.android.wallofaww.model;

public class Resource<T> {
    private T data;

    public Resource(T data, String error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {

        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    private String error;
}
