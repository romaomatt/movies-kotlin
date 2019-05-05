package com.arctouch.codechallenge.core

import android.content.res.Resources
import android.view.View

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
    return replace(".", "").toInt()
}