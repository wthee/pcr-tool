package cn.wthee.pcrtool.ui.components

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.node.Ref
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.lerp
import kotlin.math.max

// fork from accompanist placeholder


/**
 * Draws some skeleton UI which is typically used whilst content is 'loading'.
 *
 * A version of this modifier which uses appropriate values for Material themed apps is available
 * in the 'Placeholder Material' library.
 *
 * @param visible whether the placeholder should be visible or not.
 * @param shape desired shape of the placeholder. Defaults to [RectangleShape].
 * @param placeholderFadeTransitionSpec The transition spec to use when fading the placeholder
 * on/off screen. The boolean parameter defined for the transition is [visible].
 * @param contentFadeTransitionSpec The transition spec to use when fading the content
 * on/off screen. The boolean parameter defined for the transition is [visible].
 */
fun Modifier.placeholder(
    visible: Boolean,
    shape: Shape? = null,
    placeholderFadeTransitionSpec: @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
    contentFadeTransitionSpec: @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
): Modifier = composed {
    // Values used for caching purposes
    val lastSize = remember { Ref<Size>() }
    val lastLayoutDirection = remember { Ref<LayoutDirection>() }
    val lastOutline = remember { Ref<Outline>() }

    // The current highlight animation progress
    var highlightProgress: Float by remember { mutableFloatStateOf(0f) }

    // This is our crossfade transition
    val transitionState = remember { MutableTransitionState(visible) }.apply {
        targetState = visible
    }
    val transition = updateTransition(transitionState, "placeholder_crossfade")

    val placeholderAlpha by transition.animateFloat(
        transitionSpec = placeholderFadeTransitionSpec,
        label = "placeholder_fade",
        targetValueByState = { placeholderVisible -> if (placeholderVisible) 1f else 0f }
    )
    val contentAlpha by transition.animateFloat(
        transitionSpec = contentFadeTransitionSpec,
        label = "content_fade",
        targetValueByState = { placeholderVisible -> if (placeholderVisible) 0f else 1f }
    )

    // Run the optional animation spec and update the progress if the placeholder is visible
    val highlight = Shimmer(
        highlightColor = shimmerHighlightColor(),
        animationSpec = PlaceholderDefaults.shimmerAnimationSpec
    )
    val animationSpec = highlight.animationSpec
    if (visible || placeholderAlpha >= 0.01f) {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        highlightProgress = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = animationSpec,
            label = "",
        ).value
    }

    val paint = remember { Paint() }
    val mShape = shape ?: MaterialTheme.shapes.extraSmall
    val color = color()

    remember(color, shape, highlight) {
        drawWithContent {
            // Draw the composable content first
            if (contentAlpha in 0.01f..0.99f) {
                // If the content alpha is between 1% and 99%, draw it in a layer with
                // the alpha applied
                paint.alpha = contentAlpha
                withLayer(paint) {
                    with(this@drawWithContent) {
                        drawContent()
                    }
                }
            } else if (contentAlpha >= 0.99f) {
                // If the content alpha is > 99%, draw it with no alpha
                drawContent()
            }

            if (placeholderAlpha in 0.01f..0.99f) {
                // If the placeholder alpha is between 1% and 99%, draw it in a layer with
                // the alpha applied
                paint.alpha = placeholderAlpha
                withLayer(paint) {
                    lastOutline.value = drawPlaceholder(
                        shape = mShape,
                        color = color,
                        highlight = highlight,
                        progress = highlightProgress,
                        lastOutline = lastOutline.value,
                        lastLayoutDirection = lastLayoutDirection.value,
                        lastSize = lastSize.value,
                    )
                }
            } else if (placeholderAlpha >= 0.99f) {
                // If the placeholder alpha is > 99%, draw it with no alpha
                lastOutline.value = drawPlaceholder(
                    shape = mShape,
                    color = color,
                    highlight = highlight,
                    progress = highlightProgress,
                    lastOutline = lastOutline.value,
                    lastLayoutDirection = lastLayoutDirection.value,
                    lastSize = lastSize.value,
                )
            }

            // Keep track of the last size & layout direction
            lastSize.value = size
            lastLayoutDirection.value = layoutDirection
        }
    }
}

/**
 * Contains default values used by [Modifier.placeholder] and [PlaceholderHighlight].
 */
private object PlaceholderDefaults {

    /**
     * The default [InfiniteRepeatableSpec].
     */
    val shimmerAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(durationMillis = 1700, delayMillis = 200),
            repeatMode = RepeatMode.Restart
        )
    }
}

/**
 * Returns the value used as the the `highlightColor` parameter value of shimmer
 *
 * @param backgroundColor The current background color of the layout. Defaults to
 * `MaterialTheme.colorScheme.surface`.
 * @param alpha The alpha component to set on [backgroundColor]. Defaults to `0.75f`.
 */
@Composable
private fun shimmerHighlightColor(
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    alpha: Float = 0.75f,
): Color {
    return backgroundColor.copy(alpha = alpha)
}

/**
 * A class which provides a brush to paint placeholder based on progress.
 */
@Stable
private interface PlaceholderHighlight {
    /**
     * The optional [AnimationSpec] to use when running the animation for this highlight.
     */
    val animationSpec: InfiniteRepeatableSpec<Float>?

    /**
     * Return a [Brush] to draw for the given [progress] and [size].
     *
     * @param progress the current animated progress in the range of 0f..1f.
     * @param size The size of the current layout to draw in.
     */
    fun brush(
        @FloatRange(from = 0.0, to = 1.0) progress: Float,
        size: Size
    ): Brush

    /**
     * Return the desired alpha value used for drawing the [Brush] returned from [brush].
     *
     * @param progress the current animated progress in the range of 0f..1f.
     */
    @FloatRange(from = 0.0, to = 1.0)
    fun alpha(progress: Float): Float

    companion object
}


/**
 * Returns the value used as the the `color` parameter value on [Modifier.placeholder].
 *
 * @param backgroundColor The current background color of the layout. Defaults to
 * `MaterialTheme.colorScheme.surface`.
 * @param contentColor The content color to be used on top of [backgroundColor].
 * @param contentAlpha The alpha component to set on [contentColor] when compositing the color
 * on top of [backgroundColor]. Defaults to `0.1f`.
 */
@Composable
private fun color(
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    contentAlpha: Float = 0.1f,
): Color = contentColor.copy(contentAlpha).compositeOver(backgroundColor)

private fun DrawScope.drawPlaceholder(
    shape: Shape,
    color: Color,
    highlight: PlaceholderHighlight?,
    progress: Float,
    lastOutline: Outline?,
    lastLayoutDirection: LayoutDirection?,
    lastSize: Size?,
): Outline? {
    // shortcut to avoid Outline calculation and allocation
    if (shape === RectangleShape) {
        // Draw the initial background color
        drawRect(color = color)

        if (highlight != null) {
            drawRect(
                brush = highlight.brush(progress, size),
                alpha = highlight.alpha(progress),
            )
        }
        // We didn't create an outline so return null
        return null
    }

    // Otherwise we need to create an outline from the shape
    val outline = lastOutline.takeIf {
        size == lastSize && layoutDirection == lastLayoutDirection
    } ?: shape.createOutline(size, layoutDirection, this)

    // Draw the placeholder color
    drawOutline(outline = outline, color = color)

    if (highlight != null) {
        drawOutline(
            outline = outline,
            brush = highlight.brush(progress, size),
            alpha = highlight.alpha(progress),
        )
    }

    // Return the outline we used
    return outline
}

private inline fun DrawScope.withLayer(
    paint: Paint,
    drawBlock: DrawScope.() -> Unit,
) = drawIntoCanvas { canvas ->
    canvas.saveLayer(size.toRect(), paint)
    drawBlock()
    canvas.restore()
}

/**
 * The highlight starts at the top-start, and then grows to the bottom-end during the animation.
 * During that time it is also faded in, from 0f..progressForMaxAlpha, and then faded out from
 * progressForMaxAlpha..1f.
 *
 * @param highlightColor the color of the highlight 'shimmer'.
 * @param animationSpec the [AnimationSpec] to configure the animation.
 * @param progressForMaxAlpha The progress where the shimmer should be at it's peak opacity.
 * Defaults to 0.6f.
 */
private data class Shimmer(
    private val highlightColor: Color,
    override val animationSpec: InfiniteRepeatableSpec<Float>,
    private val progressForMaxAlpha: Float = 0.6f,
) : PlaceholderHighlight {
    override fun brush(
        progress: Float,
        size: Size,
    ): Brush = Brush.radialGradient(
        colors = listOf(
            highlightColor.copy(alpha = 0f),
            highlightColor,
            highlightColor.copy(alpha = 0f),
        ),
        center = Offset(x = 0f, y = 0f),
        radius = (max(size.width, size.height) * progress * 2).coerceAtLeast(0.01f),
    )

    override fun alpha(progress: Float): Float = when {
        // From 0f...ProgressForOpaqueAlpha we animate from 0..1
        progress <= progressForMaxAlpha -> {
            lerp(
                start = 0f,
                stop = 1f,
                fraction = progress / progressForMaxAlpha
            )
        }
        // From ProgressForOpaqueAlpha..1f we animate from 1..0
        else -> {
            lerp(
                start = 1f,
                stop = 0f,
                fraction = (progress - progressForMaxAlpha) / (1f - progressForMaxAlpha)
            )
        }
    }
}