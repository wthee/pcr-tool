package cn.wthee.pcrtool.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.utils.Constants

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
    visible: Boolean = true,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val animOn = mainSP().getBoolean(Constants.SP_ANIM_STATE, true)

    if (animOn) {
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = defaultSpring()
            ),
            content = content,
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(1f),
            exit = fadeOut(),
            content = content,
        )
    }

}

/**
 * 页面淡入动画
 */
@ExperimentalAnimationApi
@Composable
fun FadeAnimation(
    visible: Boolean = true,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val animOn = mainSP().getBoolean(Constants.SP_ANIM_STATE, true)

    if (animOn) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = defaultTween()),
            content = content,
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(1f),
            exit = fadeOut(),
            content = content,
        )
    }
}

/**
 * 展开动画
 */
@ExperimentalAnimationApi
@Composable
fun ExtendedAnimation(
    visible: Boolean = true,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val animOn = mainSP().getBoolean(Constants.SP_ANIM_STATE, true)

    if (animOn) {
        AnimatedVisibility(
            visible = visible,
            enter = expandIn(expandFrom = Alignment.Center, animationSpec = defaultTween()),
            exit = shrinkOut(shrinkTowards = Alignment.Center, animationSpec = defaultTween()),
            content = content,
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(1f),
            exit = fadeOut(),
            content = content,
        )
    }
}

/**
 * 菜单动画
 */
@ExperimentalAnimationApi
@Composable
fun MenuAnimation(
    visible: Boolean = true,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val animOn = mainSP().getBoolean(Constants.SP_ANIM_STATE, true)

    if (animOn) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = defaultSpring()
            ),
            exit = fadeOut(),
            content = content
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(1f),
            exit = fadeOut(),
            content = content,
        )
    }
}