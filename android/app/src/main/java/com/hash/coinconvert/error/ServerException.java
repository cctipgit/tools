package com.hash.coinconvert.error;

public class ServerException extends Throwable {
    public int code;
    public String message;

    public ServerException(int code, String message) {
        super(message);
    }
}
