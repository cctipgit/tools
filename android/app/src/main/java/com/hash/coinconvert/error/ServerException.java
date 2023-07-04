package com.hash.coinconvert.error;

import android.util.Log;

public class ServerException extends Throwable {
    public int code;
    public String message;

    public ServerException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public void printStackTrace() {
        Log.e("ServerException","code:"+code+",message:"+message);
        super.printStackTrace();
    }
}
