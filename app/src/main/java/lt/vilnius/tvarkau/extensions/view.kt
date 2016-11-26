package com.vinted.extensions

import android.view.View

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE
fun View.isGone() = this.visibility == View.GONE

/**
 * Makes [View] visible if [clause] return [true]. Otherwise invokes [or] which is [gone] by default
 */
fun View.visibleIf(visible: Boolean, or: View.() -> Unit = { gone() }) {
    if (visible) {
        visible()
    } else {
        or()
    }
}

fun View.invisibleIf(visible: Boolean) {
    visibleIf(!visible, { invisible() })
}

fun View.goneIf(visible: Boolean) {
    visibleIf(!visible)
}