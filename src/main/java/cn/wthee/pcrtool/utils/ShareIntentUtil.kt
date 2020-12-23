package cn.wthee.pcrtool.utils

import android.content.Intent

object ShareIntentUtil {

    fun text(str: String) {
        var shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            str
        )
        shareIntent = Intent.createChooser(shareIntent, "分享到：")
        //将mipmap中图片转换成Uri
//            val imgUri = FileUtil.getUriFromDrawableRes(MyApplication.context, R.drawable.app_code)
//            shareIntent.action = Intent.ACTION_SEND
//            shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri)
//            shareIntent.type = "image/*"
//            shareIntent = Intent.createChooser(shareIntent, "分享下载二维码")
        ActivityUtil.instance.currentActivity?.startActivity(shareIntent)
    }
}