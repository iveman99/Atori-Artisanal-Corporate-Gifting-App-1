package com.example.buynow.presentation.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.buynow.R


class LoadingDialog(private val context: Context) {

    private var alertDialog: AlertDialog? = null

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_loading_dialog, null)
        builder.setView(view)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog?.show()
    }

    fun dismissDialog() {
        alertDialog?.dismiss()
    }
}