package de.halfbit.co2monitor.main.utils

import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.applyInsets(
    block: (View, insets: WindowInsetsCompat, padding: Rect) -> WindowInsetsCompat
) {
    val padding = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        block(view, insets, padding)
    }
    if (isAttachedToWindow) ViewCompat.requestApplyInsets(this)
    else addOnAttachStateChangeListener(
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                view.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(view)
            }

            override fun onViewDetachedFromWindow(view: View) = Unit
        }
    )
}

fun View.consumeInsets(
    padding: Rect,
    insets: WindowInsetsCompat
): WindowInsetsCompat {
    setPadding(
        padding.left + insets.systemWindowInsetLeft,
        padding.top + insets.systemWindowInsetTop,
        padding.right + insets.systemWindowInsetRight,
        padding.bottom + insets.systemWindowInsetBottom
    )
    return insets.replaceSystemWindowInsets(
        Rect(0, 0, 0, 0)
    )
}
