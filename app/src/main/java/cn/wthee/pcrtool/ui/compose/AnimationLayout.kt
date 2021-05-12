package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable

@ExperimentalAnimationApi
@Composable
fun EnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = {
                100
            }
        ),
        exit = shrinkOut(),
        content = content,
        initiallyVisible = false
    )
}

@ExperimentalAnimationApi
@Composable
fun PopEnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = {
                -30
            }
        ),
        exit = shrinkOut(),
        content = content,
        initiallyVisible = false
    )
}