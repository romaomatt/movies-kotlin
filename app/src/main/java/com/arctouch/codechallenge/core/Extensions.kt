package com.arctouch.codechallenge.core

import android.content.res.Resources
import android.view.View
import java.util.*

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Int.dpToPx(resources: Resources): Int {
    val scale = resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun String.decimalToInt(): Int {
    return if (length == 1) {
        this.toInt() * 10
    } else {
        this.replace(".", "").toInt()
    }
}

fun String.formatDateFromTmdb(): String {
    return if (length == 10) {
        if (Locale.getDefault().country == "BR") {
            val dateList = this.split("-")
            dateList.reversed().joinToString("/")
        } else {
            this.replace("-", "/")
        }
    } else {
        ""
    }
}