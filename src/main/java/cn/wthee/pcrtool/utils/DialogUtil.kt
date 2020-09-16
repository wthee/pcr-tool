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

    //创建 dialog
    fun create(context: Context, layout: View, gravity: Int): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(layout)
            .create()
        dialog.window?.setGravity(gravity)
        return dialog
    }

    fun create(
        context: Context,
        layout: LayoutWarnDialogBinding,
        title: String?,
        content: String,
        btn1: String,
        btn2: String,
        listener: DialogListener
    ): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(layout.root)
            .create()
        //内容设置
        layout.title.text = title
        layout.message.text = content
        layout.dialogOperate.text = btn1
        layout.dialogNext.text = btn2

        //按钮监听
        layout.dialogOperate.setOnClickListener {
            listener.onButtonOperateClick(dialog)
        }
        layout.dialogNext.setOnClickListener {
            listener.onButtonOkClick(dialog)
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setCancelable(false)
        return dialog
    }
}

interface DialogListener{

    fun onButtonOperateClick(dialog: AlertDialog)

    fun onButtonOkClick(dialog: AlertDialog)
}
