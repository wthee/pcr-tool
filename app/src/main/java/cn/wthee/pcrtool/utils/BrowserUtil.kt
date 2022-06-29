package cn.wthee.pcrtool.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import cn.wthee.pcrtool.R

object BrowserUtil {

    /**
     * 在浏览器中打开 [url]
     *
     * @param url 链接
     * @param title 标题
     */
    fun open(context: Context, url: String, title: String = "请选择浏览器") {
        val packageList = getCustomTabsPackages(context)

        if (packageList.isNotEmpty()) {
            val builder = CustomTabsIntent.Builder().apply {
                setStartAnimations(context, R.anim.fade_in, R.anim.fade_out)
                setExitAnimations(context, R.anim.fade_out, R.anim.fade_in)
                setShowTitle(true)
            }

            val customTabsIntent = builder.build()
            //设置默认应用
            customTabsIntent.intent.setPackage(packageList[0].activityInfo.packageName)
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } else {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(Intent.createChooser(intent, title))
        }
    }

    /**
     * 获取支持自定义标签的应用信息
     */
    private fun getCustomTabsPackages(context: Context): ArrayList<ResolveInfo> {
        val pm = context.packageManager
        // Get default VIEW intent handler.
        val activityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs: ArrayList<ResolveInfo> = ArrayList()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info)
            }
        }
        return packagesSupportingCustomTabs
    }

}