package co.natsuhi.mushroomstep.utils;

import android.util.Log;

import co.natsuhi.mushroomstep.BuildConfig;

public class LogUtil {
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
