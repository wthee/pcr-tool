package cn.wthee.pcrtool.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * 弹窗工具
 */
object DialogUtil {

    /**
     *  创建自定义 [layout] 弹窗
     */
    fun create(context: Context, layout: View, gravity: Int = Gravity.BOTTOM): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(layout)
            .create()
        dialog.window?.setGravity(gravity)
        return dialog
    }

    /**
     * 创建 [layout] 弹窗，并设置
     * 点击回调 [listener]
     */
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
        return dialog
    }
}

/**
 * 点击回调
 */
interface DialogListener {

    fun onCancel(dialog: AlertDialog)

    fun onConfirm(dialog: AlertDialog)
}
