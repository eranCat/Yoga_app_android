package com.erank.yogappl.ui.custom_views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.erank.yogappl.R
import kotlinx.android.synthetic.main.progress_layout.*


class ProgressDialog(context: Context) :
    Dialog(context, R.style.CustomDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_layout)

        setCancelable(false)
    }

    fun setText(text: String): ProgressDialog {
        pb_text.text = text
        return this
    }
}