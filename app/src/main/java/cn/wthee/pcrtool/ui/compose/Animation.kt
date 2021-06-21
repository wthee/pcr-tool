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
    return tween(durationMillis = 350, easing = FastOutLinearInEasing)
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
    )
}


/**
 * 页面进入动画
 */
@ExperimentalAnimationApi
@Composable
fun SlideLeftAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = if (animOn) {
            slideInHorizontally(initialOffsetX = {
                40
            }, defaultSpring()) + fadeIn(0f)
        } else {
            fadeIn(1f)
        },
        exit = if (animOn) {
            slideOutHorizontally(targetOffsetX = {
                40
            }, defaultSpring()) + fadeOut(0f)
        } else {
            fadeOut(0f)
        },
        content = content,
    )
}