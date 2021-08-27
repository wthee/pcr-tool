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
    return tween(durationMillis = 200, easing = FastOutSlowInEasing)
}


//页面退出动画
@ExperimentalAnimationApi
val fadeOut = if (animOn) {
    fadeOut(animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing))
} else {
    fadeOut(targetAlpha = 1f)
}

//页面进入动画：从小向上滚动
@ExperimentalAnimationApi
val slideIn = if (animOn) {
    slideInVertically(
        initialOffsetY = { 30 },
        animationSpec = defaultTween()
    )
} else {
    fadeIn(initialAlpha = 1f)
}

//页面进入动画：渐入
@ExperimentalAnimationApi
val fadeIn = if (animOn) {
    fadeIn(animationSpec = defaultTween())
} else {
    fadeIn(initialAlpha = 1f)
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
        enter = slideIn,
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
        enter = fadeIn,
        exit = fadeOut(),
        content = content,
        modifier = modifier
    )
}