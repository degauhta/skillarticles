package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup

fun View.setMarginOptionally(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    if (this !is ViewGroup) return
    val lp = layoutParams as ViewGroup.MarginLayoutParams ?: return
    lp.setMargins(left, top, right, bottom)
    layoutParams = lp
}