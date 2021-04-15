package cn.wthee.pcrtool.utils

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.permissionx.guolindev.PermissionX

/**
 * 分享
 */
object ShareIntentUtil {

    /**
     * 分享文本
     */
    fun text(str: String) {
        var shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            str
        )
        shareIntent = Intent.createChooser(shareIntent, "分享到：")
        ActivityHelper.instance.currentActivity?.startActivity(shareIntent)
    }

    /**
     * 分享图片
     */
    fun image(activity: FragmentActivity, view: View, fileName: String) {
        //获取要分享的视图 bitmap
        val bitmap = ScreenshotUtil.getBitmap(view)
        shareImage(activity, bitmap, fileName)
    }

    /**
     * 分享 RecyclerView
     */
    fun imageLong(activity: FragmentActivity, view: RecyclerView, fileName: String) {
        //获取要分享的视图 bitmap
        val bitmap = ScreenshotUtil.shotRecyclerView(view)
        shareImage(activity, bitmap, fileName)
    }

    /**
     * 分享图片
     */
    private fun shareImage(activity: FragmentActivity, bitmap: Bitmap?, fileName: String) {
        if (bitmap == null) {
            ToastUtil.short("分享出错，请重试~")
        } else {
            PermissionX.init(activity).permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).request { allGranted, _, _ ->
                if (allGranted) {
                    //保存到本地
                    val saved = ImageDownloadHelper(activity).saveBitmap(bitmap, fileName, false)
                    if (saved) {
                        val path = ImageDownloadHelper.getImagePath()
                        val uri = Uri.parse("file:///$path/$fileName")
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        shareIntent.type = "image/*"
                        //分享
                        activity.startActivity(Intent.createChooser(shareIntent, "图片已保存，可选择分享"))
                    }
                } else {
                    ToastUtil.short("无法保存~请允许相关权限")
                }
            }
        }
    }
}