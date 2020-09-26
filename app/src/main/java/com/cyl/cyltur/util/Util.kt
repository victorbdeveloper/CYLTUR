package com.cyl.cyltur.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cyl.cyltur.R
import kotlin.math.roundToInt

fun AppCompatActivity.newProgressDialog(): Dialog? {
    val dialog = Dialog(this, R.style.FullScreenDialogTheme)
    dialog.setCancelable(false)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    dialog.setContentView(R.layout.dialog_loading)
    return dialog.showDialog(this)
}

fun Fragment.newProgressDialog(): Dialog? = (context as? AppCompatActivity)?.newProgressDialog()

fun Dialog.showDialog(activity: AppCompatActivity?): Dialog? {
    if (!this.isShowing && activity?.isFinishing == false) {
        show()
    }
    return this
}

fun Dialog.dismissDialog() {
    if (isShowing) {
        dismiss()
    }
}

fun Context.convertDpToPixel(dp: Float): Int {
    val metrics = resources.displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return px.roundToInt()
}