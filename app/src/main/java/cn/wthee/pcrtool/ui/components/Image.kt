package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.imageLoader
import coil3.request.ErrorResult
import coil3.request.SuccessResult

const val RATIO = 16 / 9f

// 741 * 1200
const val RATIO_COMIC = 0.64f
const val RATIO_BANNER = 1024 / 587f
const val RATIO_TEASER = 1024 / 430f
const val SCALE_LOGO = 2.2f


/**
 * 图片加载
 */
@Composable
fun MainImage(
    modifier: Modifier = Modifier,
    data: String,
    ratio: Float?,
    contentScale: ContentScale = ContentScale.Fit,
    placeholder: Boolean = true,
    onError: (ErrorResult) -> Unit = {},
    onSuccess: (SuccessResult) -> Unit = {},
) {

    val loading = remember {
        mutableStateOf(true)
    }
    val loader = LocalContext.current.imageLoader

    AsyncImage(
        model = data,
        contentDescription = null,
        contentScale = contentScale,
        filterQuality = FilterQuality.None,
        error = rememberAsyncImagePainter(R.drawable.error, contentScale = contentScale),
        onSuccess = {
            loading.value = false
            onSuccess(it.result)
        },
        onError = {
            loading.value = false
            loader.diskCache?.remove(data)
            onError(it.result)
        },
        onLoading = {
            loading.value = true
        },
        modifier = modifier
            .then(
                if (ratio != null) {
                    Modifier.aspectRatio(ratio)
                } else {
                    Modifier
                }
            )
            .then(
                if (placeholder && loading.value) {
                    Modifier
                        .aspectRatio(ratio ?: RATIO)
                        .placeholder(visible = loading.value)
                } else {
                    Modifier
                }
            )
    )
}

/**
 * 角色位置图标
 */
@Composable
fun PositionIcon(modifier: Modifier = Modifier, position: Int, size: Dp = Dimen.smallIconSize) {
    val positionIconId = PositionType.getPositionType(position).iconId

    MainIcon(
        data = positionIconId,
        size = size,
        modifier = modifier
    )
}

/**
 * 图标
 * @param tint [MainIconType]类型适用
 * @param colorFilter 其他类型图标中适用
 * @param contentScale 图标缩放，非ImageVector才生效
 */
@Composable
fun MainIcon(
    modifier: Modifier = Modifier,
    data: Any,
    size: Dp = Dimen.iconSize,
    tint: Color = MaterialTheme.colorScheme.primary,
    colorFilter: ColorFilter? = null,
    wrapSize: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val shape = MaterialTheme.shapes.extraSmall

    val mModifier = modifier
        .clip(shape)
        .then(
            if (onClick != null) {
                Modifier
                    .clickable(onClick = {
                        VibrateUtil(context).single()
                        onClick()
                    })
            } else {
                Modifier
            }
        )
        .then(
            if (!wrapSize) {
                Modifier.size(size)
            } else {
                Modifier
            }
        )


    when (data) {
        is MainIconType -> {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                tint = tint,
                modifier = mModifier
            )
        }

        is ImageVector -> {
            Icon(
                imageVector = data,
                contentDescription = null,
                tint = tint,
                modifier = mModifier
            )
        }

        else -> {
            val loading = remember {
                mutableStateOf(true)
            }

            AsyncImage(
                model = data,
                colorFilter = colorFilter,
                contentDescription = null,
                contentScale = contentScale,
                filterQuality = FilterQuality.None,
                error = rememberAsyncImagePainter(
                    R.drawable.unknown_item,
                    contentScale = contentScale
                ),
                onError = {
                    it.result.throwable
                    loading.value = false
                },
                onSuccess = {
                    loading.value = false
                },
                onLoading = {
                    loading.value = true
                },
                modifier = mModifier
                    .aspectRatio(1f)
                    .placeholder(visible = loading.value, shape = shape)
            )
        }
    }
}