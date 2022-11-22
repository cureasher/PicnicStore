package com.oh.app.common

import android.widget.Toast

fun toastMessage(message: String) {
    Toast.makeText(PicnicStoreApplication.getAppInstance(), message, Toast.LENGTH_SHORT).show()
}