package com.alphawallet.app.util;

import android.util.Log;

import com.tencent.upgrade.callback.Logger;

/**
 * Created by raymondhu on 2024/1/5
 */
public class ShiplyLogger implements Logger {
    private final String TAG_PREFIX = "ShiplyLogger_";
    
    @Override
    public void v(String tag, String msg) {
        Log.v(TAG_PREFIX + tag, msg);
    }

    @Override
    public void v(String tag, String msg, Throwable throwable) {
        Log.v(TAG_PREFIX + tag, msg, throwable);
    }

    @Override
    public void d(String tag, String msg) {
        Log.d(TAG_PREFIX + tag, msg);
    }

    @Override
    public void d(String tag, String msg, Throwable throwable) {
        Log.d(TAG_PREFIX + tag, msg, throwable);
    }

    @Override
    public void i(String tag, String msg) {
        Log.i(TAG_PREFIX + tag, msg);
    }

    @Override
    public void i(String tag, String msg, Throwable throwable) {
        Log.i(TAG_PREFIX + tag, msg, throwable);
    }

    @Override
    public void w(String tag, String msg) {
        Log.w(TAG_PREFIX + tag, msg);
    }

    @Override
    public void w(String tag, String msg, Throwable throwable) {
        Log.w(TAG_PREFIX + tag, msg, throwable);
    }

    @Override
    public void e(String tag, String msg) {
        Log.e(TAG_PREFIX + tag, msg);
    }

    @Override
    public void e(String tag, String msg, Throwable throwable) {
        Log.e(TAG_PREFIX + tag, msg, throwable);
    }
}
