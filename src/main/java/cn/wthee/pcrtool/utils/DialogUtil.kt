package cn.wthee.pcrtool.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtil {
    //创建 dialog
    fun create(context: Context, layout: View): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(layout)
            .create()
        dialog.window?.setGravity(Gravity.BOTTOM)
        return dialog
    }

    fun create(
        context: Context,
        layout: LayoutWarnDialogBinding,
        title: String?,
        content: String?
    ): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(layout.root)
            .create()
        val mTitle = layout.title
        val mMessage = layout.message
        val mNext = layout.dialogNext
        mTitle.text = title
        mMessage.text = content
        mNext.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        return dialog
    }
}