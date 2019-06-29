package com.rakuishi.behavior

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

fun View.clipToOval() {

    this.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(
                0,
                0,
                view.width,
                view.height
            )
        }
    }
    this.clipToOutline = true
}