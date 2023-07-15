package com.dicoding.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged


class MyPasswordEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Input your password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {

        doOnTextChanged { text, _, _, _ ->
            error = if (text.toString().length < 8) {
                "Password must contain at least 8 characters"
            } else {
                null
            }
        }
    }
}