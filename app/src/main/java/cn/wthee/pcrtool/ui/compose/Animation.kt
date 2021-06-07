package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOn
import cn.wthee.pcrtool.utils.ScreenUtil

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
 * 展开动画
 */
@ExperimentalAnimationApi
@Composable
fun ExtendedAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = if (animOn) {
            expandIn(expandFrom = Alignment.Center, animationSpec = defaultTween())
        } else {
            fadeIn(1f)
        },
        exit = shrinkOut(shrinkTowards = Alignment.Center, animationSpec = defaultTween()),
        content = content,
    )
}

/**
 * 菜单动画
 */
@ExperimentalAnimationApi
@Composable
fun MenuAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = if (animOn) {
            slideInVertically(
                initialOffsetY = { 100 },
                animationSpec = defaultSpring()
            )
        } else {
            fadeIn(1f)
        },
        exit = fadeOut(),
        content = content
    )

}


/**
 * 从上至下移动
 */
@ExperimentalAnimationApi
@Composable
fun SlideDownAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    val halfImageHeight = ScreenUtil.getCharacterCardHeight().toInt() / 2
    AnimatedVisibility(
        visible = visible,
        enter = if (animOn) {
            slideInVertically(initialOffsetY = {
                -it / 2 + halfImageHeight
            }, defaultTween()) + fadeIn(0.5f)
        } else {
            fadeIn(1f)
        },
        exit = fadeOut(),
        content = content
    )

}