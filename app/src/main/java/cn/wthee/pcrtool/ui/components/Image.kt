package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ErrorResult
import coil.request.SuccessResult

const val RATIO = 16 / 9f

// 741 * 1200
const val RATIO_COMIC = 0.63f
const val RATIO_COMMON = 371 / 208f
const val RATIO_BANNER = 1024 / 587f
const val RATIO_TEASER = 1024 / 430f


/**
 * 图片加载
 */
@Composable
fun MainImage(
    modifier: Modifier = Modifier,
    data: String,
    ratio: Float,
    contentScale: ContentScale = ContentScale.FillWidth,
    placeholder: Boolean = true,
    onError: (ErrorResult) -> Unit = {},
    onSuccess: (SuccessResult) -> Unit = {},
) {

    var mModifier = modifier
    if (ratio > 0) {
        mModifier = modifier
            .aspectRatio(ratio)
    }
    val loading = remember {
        mutableStateOf(true)
    }

    AsyncImage(
        model = data,
        contentDescription = null,
        contentScale = contentScale,
        error = rememberAsyncImagePainter(R.drawable.error, contentScale = contentScale),
        onSuccess = {
            loading.value = false
            onSuccess(it.result)
        },
        onError = {
            loading.value = false
            onError(it.result)
        },
        onLoading = {
            loading.value = true
        },
        modifier = if (placeholder) {
            mModifier.commonPlaceholder(visible = loading.value)
        } else {
            mModifier
        }
    )
}

/**
 * 角色位置图标
 */
@Composable
fun PositionIcon(modifier: Modifier = Modifier, position: Int, size: Dp = Dimen.smallIconSize) {
    val positionIconId = when (PositionType.getPositionType(position)) {
        PositionType.POSITION_0_299 -> R.drawable.ic_position_0
        PositionType.POSITION_300_599 -> R.drawable.ic_position_1
        PositionType.POSITION_600_999 -> R.drawable.ic_position_2
        PositionType.UNKNOWN -> R.drawable.unknown_item
    }

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
 */
@Composable
fun MainIcon(
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
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick()
            })
    } else {
        modifier.clip(MaterialTheme.shapes.extraSmall)
    }
    mModifier = if (!wrapSize) {
        mModifier.size(size)
    } else {
        mModifier.sizeIn(maxWidth = size, maxHeight = size)
    }


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
            val contentScale = ContentScale.Crop
            val loading = remember {
                mutableStateOf(true)
            }

            AsyncImage(
                model = data,
                colorFilter = colorFilter,
                contentDescription = null,
                contentScale = contentScale,
                error = rememberAsyncImagePainter(
                    R.drawable.unknown_item,
                    contentScale = contentScale
                ),
                onError = {
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
                    .commonPlaceholder(visible = loading.value)
            )
        }
    }
}

/**
 * 图片
 * @param loading 保存原图时使用，等原图缓存结束
 */
@Composable
fun SubImage(
    modifier: Modifier = Modifier,
    data: Any,
    contentScale: ContentScale = ContentScale.Fit,
    loading: MutableState<Boolean>? = null,
    ratio: Float? = null,
    onSuccess: (SuccessResult) -> Unit = {}
) {

    Box {
        SubcomposeAsyncImage(
            model = data,
            contentDescription = null,
            contentScale = contentScale,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(RATIO)
                        .commonPlaceholder(true)
                )
            },
            error = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = R.drawable.error,
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(RATIO)
                    )
                }
                loading?.value = false
            },
            onSuccess = {
                onSuccess(it.result)
            },
            modifier = if (ratio != null) {
                modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio)
            } else {
                modifier.fillMaxWidth()
            }
        )

        //加载立绘原图时
        if (loading != null && loading.value) {
            CircularProgressCompose(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Dimen.smallPadding),
                size = Dimen.textIconSize
            )
        }
    }

}