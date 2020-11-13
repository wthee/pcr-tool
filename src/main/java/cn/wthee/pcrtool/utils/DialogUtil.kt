package cn.wthee.pcrtool.utils

import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

//TODO 优化扩展性
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
    fun create(
        context: Context,
        layout: View,
        btn1: String,
        btn2: String,
        listener: DialogListener
    ): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(layout)
            .create()
        dialog.apply {
            //取消
            setButton(DialogInterface.BUTTON_NEUTRAL, btn1) { dialog, which ->
                listener.onCancel(this)
                dialog.dismiss()
            }
            //确认
            setButton(DialogInterface.BUTTON_POSITIVE, btn2) { dialog, which ->
                listener.onConfirm(this)
                dialog.dismiss()
            }
        }
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
        layout.dialogBtns.left.text = btn1
        layout.dialogBtns.right.text = btn2

        //按钮监听
        layout.dialogBtns.left.setOnClickListener {
            listener.onCancel(dialog)
        }
        layout.dialogBtns.right.setOnClickListener {
            listener.onConfirm(dialog)
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setCancelable(false)
        return dialog
    }
}

interface DialogListener {

    fun onCancel(dialog: AlertDialog)

    fun onConfirm(dialog: AlertDialog)
}
