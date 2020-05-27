package com.erank.yogappl.utils.extensions

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
suspend fun <T> Task<T>.await(): T? {
    if (isComplete) {
        exception?.let { throw it }

        if (!isCanceled) return result

        throw CancellationException("Task $this was cancelled normally.")
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            exception?.let { cont.resumeWithException(it) }
                ?: with(cont) {
                    if (isCanceled) cancel()
                    else resume(result) {}
                }
        }
    }
}