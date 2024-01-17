package cn.wthee.pcrtool.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


/**
 * 从桌面返回监听
 */
@Composable
fun AppResumeEffect(handler: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        var recreate = false
        val observer = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_PAUSE) {
                recreate = false
            }
            if (e == Lifecycle.Event.ON_CREATE) {
                recreate = true
            }
            //首次加载 或 从桌面重新进入（不经过 ON_CREATE）
            if (!recreate && e == Lifecycle.Event.ON_START) {
                handler()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


/**
 * 生命周期监听
 */
@Composable
fun LifecycleEffect(vararg events: Lifecycle.Event, handler: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        var handled = false
        val observer = LifecycleEventObserver { _, e ->
            //暂停时，重置状态
            if (e == Lifecycle.Event.ON_PAUSE) {
                handled = false
            }
            if (events.contains(e) && !handled) {
                handled = true
                handler()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
