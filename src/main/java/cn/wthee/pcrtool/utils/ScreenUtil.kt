package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication



val Int.dp: Float
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this * scale + 0.5f)
    }

val Int.px: Float
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this / scale + 0.5f)
    }
