package cn.wthee.pcrtool.ui.theme

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
    return tween(durationMillis = 300, easing = FastOutSlowInEasing)
}

fun <T> fastTween(): TweenSpec<T> {
    return tween(durationMillis = 100, easing = LinearEasing)
}


//页面退出动画
val fadeOut = fadeOut(animationSpec = fastTween())

//页面进入动画：从下向上滚动
val mySlideIn = if (animOn) {
    slideInVertically(
        initialOffsetY = { 30 },
        animationSpec = defaultTween()
    )
} else {
    fadeIn(animationSpec = fastTween())
}

//页面进入动画：渐入
val myFadeIn = if (animOn) {
    fadeIn(animationSpec = defaultTween())
} else {
    fadeIn(animationSpec = fastTween())
}

/**
 * 页面进入动画
 */
@Composable
fun SlideAnimation(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = mySlideIn,
        exit = fadeOut(),
        content = content,
    )
}

/**
 * 页面淡入动画
 */
@Composable
fun FadeAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = myFadeIn,
        exit = fadeOut(),
        content = content,
        modifier = modifier
    )
}