package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOn

/**
 * 动画弹性
 */
fun <T> defaultSpring(): SpringSpec<T> {
    return spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
}

/**
 * 持续时间
 */
fun <T> defaultTween(): TweenSpec<T> {
    return tween(durationMillis = 200, easing = FastOutLinearInEasing)
}

/**
 * 页面进入动画
 */
@ExperimentalAnimationApi
@Composable
fun SlideAnimation(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (animOn) {
            slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = defaultTween()
            )
        } else {
            fadeIn(1f)
        },
        exit = fadeOut(),
        content = content,
    )
}

/**
 * 页面淡入动画
 */
@ExperimentalAnimationApi
@Composable
fun FadeAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = if (animOn) {
            fadeIn(animationSpec = defaultTween())
        } else {
            fadeIn(1f)
        },
        exit = fadeOut(),
        content = content,
        modifier = modifier
    )
}