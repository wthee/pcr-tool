package cn.wthee.pcrtool.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.utils.VibrateUtil
import coil.compose.AsyncImage
import coil.request.ImageRequest

const val RATIO = 1.78f

// 741 * 1200
const val RATIO_COMIC = 0.6175f
const val RATIO_COMMON = 371 / 208f
const val RATIO_BANNER = 1024 / 682f


@Composable
fun ImageCompose(
    modifier: Modifier = Modifier,
    data: Any,
    ratio: Float,
    @DrawableRes loadingId: Int? = null,
    @DrawableRes errorId: Int? = null,
    contentScale: ContentScale = ContentScale.FillWidth,
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(data)
            .listener(
                onSuccess = { _, _ ->
                    onSuccess.invoke()
                }
            )
            .build(),
        contentDescription = null,
        contentScale = contentScale,
        loading = {
            AsyncImage(
                model = loadingId,
                contentDescription = null,
                contentScale = contentScale,
                modifier = modifier
                    .aspectRatio(ratio)
            )
        },
        error = {
            AsyncImage(
                model = errorId,
                contentDescription = null,
                contentScale = contentScale,
                modifier = modifier
                    .aspectRatio(ratio)
            )
        },
        modifier = modifier
            .aspectRatio(ratio)
    )
}


/**
 * 角色位置图标
 */
@Composable
fun PositionIcon(modifier: Modifier = Modifier, position: Int, size: Dp = Dimen.smallIconSize) {
    val positionIconId = when (position) {
        in 0..299 -> R.drawable.ic_position_0
        in 300..599 -> R.drawable.ic_position_1
        in 600..9999 -> R.drawable.ic_position_2
        else -> R.drawable.ic_position_2
    }
    IconCompose(
        data = positionIconId,
        size = size,
        modifier = modifier
    )
}

/**
 * 图标
 */
@Composable
fun IconCompose(
    modifier: Modifier = Modifier,
    data: Any,
    size: Dp = Dimen.iconSize,
    tint: Color = MaterialTheme.colorScheme.primary,
    colorFilter: ColorFilter? = null,
    wrapSize: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var mModifier = if (onClick != null) {
        modifier
            .clip(Shape.small)
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick.invoke()
            })
    } else {
        modifier.clip(Shape.small)
    }
    if (!wrapSize) {
        mModifier = mModifier.size(size)
    }


    if (data is ImageVector) {
        Icon(
            imageVector = data,
            contentDescription = null,
            tint = tint,
            modifier = mModifier
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(data)
                .build(),
            colorFilter = colorFilter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = {
                AsyncImage(
                    model = R.drawable.unknown_gray,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = mModifier
                )
            },
            error = {
                AsyncImage(
                    model = R.drawable.unknown_gray,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = mModifier
                )
            },
            modifier = mModifier
        )
    }
}
