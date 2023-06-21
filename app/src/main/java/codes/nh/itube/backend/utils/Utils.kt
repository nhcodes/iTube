package codes.nh.itube.backend.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.Future


//log

private const val LOG_TAG = "===[iTube]==="

fun LOG(message: String) {
    Log.d(LOG_TAG, message)
}

//async

private val EXECUTOR_SERVICE = Executors.newCachedThreadPool()

fun async(f: () -> Unit): Future<*>? {
    return EXECUTOR_SERVICE.submit {
        try {
            f()
        } catch (e: Exception) {
            LOG("async error: ${e.stackTraceToString()}")
        }
    }
}

fun sync(f: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        f()
    }
}