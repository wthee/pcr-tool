package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.*
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
        exit = slideOutVertically(targetOffsetY = {
            100
        }),
        content = content,
        initiallyVisible = false
    )
}

@ExperimentalAnimationApi
@Composable
fun PopEnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(0f),
        exit = fadeOut(0f),
        content = content,
        initiallyVisible = false
    )
}