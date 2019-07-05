package com.androidhuman.example.simplegithub.util;

import android.util.Log;

import com.androidhuman.example.simplegithub.BuildConfig;

import java.util.Locale;


public final class LogMsg {
    public static boolean DEBUG_BUILD = BuildConfig.DEBUG;

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NO_LOG = 6;  // 로그메시지 표시 안함

    private static int MSG_LEVEL = VERBOSE;   // 초기값.  WARN -> VERBOSE


    public static void setLogMsgStyle(int nLogMsgLevel) {
        MSG_LEVEL = nLogMsgLevel;
    }

    /**
     * Do not send a log message.
     */
    public static void x(String tag, String msg) {
        return;
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (VERBOSE < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.v(tag, totalMsg);

    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (VERBOSE < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.v(tag, totalMsg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (DEBUG < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.d(tag, totalMsg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.d(tag, totalMsg, tr);
    }

    /**
     * Send a {@link #INFO} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (INFO < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.i(tag, totalMsg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (INFO < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.i(tag, totalMsg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (WARN < MSG_LEVEL)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.w(tag, totalMsg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (WARN < MSG_LEVEL)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.w(tag, totalMsg, tr);
    }

    /**
     * Send a {@link #ERROR} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (ERROR < MSG_LEVEL)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.e(tag, totalMsg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (ERROR < MSG_LEVEL)
            return;
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String totalMsg = makeMsg(msg, ste);
        Log.e(tag, totalMsg, tr);
    }

    private static String makeMsg(String msg, StackTraceElement[] ste) {
        String strMsg = "";
        strMsg += String.format(Locale.getDefault(), "      [%s:%d][%s()] #[%s]#", ste[1].getFileName(), ste[1].getLineNumber(), ste[1].getMethodName(), msg);

        return strMsg;
    }

}
