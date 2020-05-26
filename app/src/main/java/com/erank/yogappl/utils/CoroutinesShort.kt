package com.erank.yogappl.utils

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun runOnBackground(runnable: suspend () -> Unit, postExec: (() -> Unit)? = null) {
    GlobalScope.launch(IO) {
        runnable()

        withContext(Main) {
            postExec?.invoke()
        }
    }
}

fun <T> runOnBackground(runnable: suspend () -> T, postExec: ((T) -> Unit)) {
    GlobalScope.launch(IO) {
        val data = runnable()
        withContext(Main) {
            postExec(data)
        }
    }
}