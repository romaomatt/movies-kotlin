package com.arctouch.codechallenge.core

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
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

fun Rect.configureGridMargins(isItemOnLeft: Boolean, marginMax: Int, marginMin: Int) {
    left = if (isItemOnLeft) marginMax else marginMin
    right = if (isItemOnLeft) marginMin else marginMax
    top = marginMin
    bottom = marginMin
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}