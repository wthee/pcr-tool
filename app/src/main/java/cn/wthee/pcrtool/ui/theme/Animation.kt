package cn.wthee.pcrtool.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag

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
fun <T> defaultTween(durationMillis: Int = 400): TweenSpec<T> {
    return tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)
}

/**
 * 页面进入动画：渐入
 */
val myFadeIn = fadeIn(animationSpec = defaultTween())

/**
 * 页面退出动画
 */
val myFadeOut = fadeOut(animationSpec = defaultTween())

/**
 * 展开
 */
val myExpandIn = expandVertically(
    expandFrom = Alignment.Top,
    animationSpec = defaultSpring()
) + myFadeIn

/**
 * 折叠
 */
val myShrinkIn = shrinkVertically(
    shrinkTowards = Alignment.Top,
    animationSpec = defaultSpring()
) + myFadeOut


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
        enter = if (animOnFlag) myFadeIn else noAnimIn(),
        exit = if (animOnFlag) myFadeOut else noAnimOut(),
        content = content,
        modifier = modifier
    )
}

/**
 * 页面展开动画，从上到下
 */
@Composable
fun ExpandAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (animOnFlag) myExpandIn else noAnimIn(),
        exit = if (animOnFlag) myShrinkIn else noAnimOut(),
        content = content,
    )
}


/**
 * 缩放动画
 */
@Composable
fun ScaleBottomEndAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (animOnFlag) {
            scaleIn(
                animationSpec = defaultTween(300),
                transformOrigin = TransformOrigin(1f, 1f)
            ) + myFadeIn
        } else {
            noAnimIn()
        },
        exit = if (animOnFlag) {
            scaleOut(
                animationSpec = defaultTween(180),
                transformOrigin = TransformOrigin(1f, 1f),
                targetScale = 0.6f
            ) + fadeOut(defaultTween(180))
        } else {
            noAnimOut()
        },
        content = content,
    )
}

/**
 * 减弱进入动画效果
 */
private fun noAnimIn() = fadeIn(animationSpec = defaultTween(80))

/**
 * 减弱退出动画效果
 */
private fun noAnimOut() = fadeOut(animationSpec = defaultTween(80))


/**
 * 导航动画
 */
fun enterTransition() = if (animOnFlag) {

    scaleIn(
        initialScale = 0.86f,
        animationSpec = defaultTween(300)
    ) + slideInVertically(defaultTween(600)) {
        75
    } + myFadeIn
} else {
    noAnimIn()
}

fun exitTransition() = noAnimOut()
