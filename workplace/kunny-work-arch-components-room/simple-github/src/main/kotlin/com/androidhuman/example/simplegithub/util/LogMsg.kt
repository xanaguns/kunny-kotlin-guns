package com.androidhuman.example.simplegithub.util

import android.util.Log

import com.androidhuman.example.simplegithub.BuildConfig

import java.util.Locale


object LogMsg {
    var DEBUG_BUILD = BuildConfig.DEBUG

    val VERBOSE = 1
    val DEBUG = 2
    val INFO = 3
    val WARN = 4
    val ERROR = 5
    val NO_LOG = 6  // 로그메시지 표시 안함

    private var MSG_LEVEL = VERBOSE   // 초기값.  WARN -> VERBOSE


    fun setLogMsgStyle(nLogMsgLevel: Int) {
        MSG_LEVEL = nLogMsgLevel
    }

    /**
     * Do not send a log message.
     */
    fun x(tag: String, msg: String) {
        return
    }

    /**
     * Send a [.VERBOSE] log message.
     *
     * @param msg The message you would like logged.
     */
    fun v(tag: String, msg: String) {
        if (VERBOSE < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.v(tag, totalMsg)

    }

    /**
     * Send a [.VERBOSE] log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun v(tag: String, msg: String, tr: Throwable) {
        if (VERBOSE < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.v(tag, totalMsg, tr)
    }

    /**
     * Send a [.DEBUG] log message.
     *
     * @param msg The message you would like logged.
     */
    fun d(tag: String, msg: String) {
        if (DEBUG < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.d(tag, totalMsg)
    }

    /**
     * Send a [.DEBUG] log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun d(tag: String, msg: String, tr: Throwable) {
        if (DEBUG < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.d(tag, totalMsg, tr)
    }

    /**
     * Send a [.INFO] log message.
     *
     * @param msg The message you would like logged.
     */
    fun i(tag: String, msg: String) {
        if (INFO < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.i(tag, totalMsg)
    }

    /**
     * Send a [.INFO] log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun i(tag: String, msg: String, tr: Throwable) {
        if (INFO < MSG_LEVEL /*&& !DEBUG_BUILD*/)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.i(tag, totalMsg, tr)
    }

    /**
     * Send a [.WARN] log message.
     *
     * @param msg The message you would like logged.
     */
    fun w(tag: String, msg: String) {
        if (WARN < MSG_LEVEL)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.w(tag, totalMsg)
    }

    /**
     * Send a [.WARN] log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun w(tag: String, msg: String, tr: Throwable) {
        if (WARN < MSG_LEVEL)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.w(tag, totalMsg, tr)
    }

    /**
     * Send a [.ERROR] log message.
     *
     * @param msg The message you would like logged.
     */
    fun e(tag: String, msg: String) {
        if (ERROR < MSG_LEVEL)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.e(tag, totalMsg)
    }

    /**
     * Send a [.ERROR] log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun e(tag: String, msg: String, tr: Throwable) {
        if (ERROR < MSG_LEVEL)
            return
        val ste = Throwable().stackTrace
        val totalMsg = makeMsg(msg, ste)
        Log.e(tag, totalMsg, tr)
    }

    private fun makeMsg(msg: String, ste: Array<StackTraceElement>): String {
        var strMsg = ""
        strMsg += String.format(Locale.getDefault(), "      [%s:%d][%s()] #[%s]#", ste[1].fileName, ste[1].lineNumber, ste[1].methodName, msg)

        return strMsg
    }

}
