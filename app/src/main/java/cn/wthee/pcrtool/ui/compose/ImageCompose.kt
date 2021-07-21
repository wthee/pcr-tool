package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.VibrateUtil
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

const val RATIO = 1.78f

// 741 * 1200
const val RATIO_COMIC = 0.6175f
const val RATIO_COMMON = 371 / 208f


@ExperimentalCoilApi
@Composable
fun ImageCompose(url: String, hasRatio: Boolean = false) {
    val painter = rememberImagePainter(
        data = url,
        builder = {
            crossfade(Constants.FADE_TIME)
        })
    val modifier = if (hasRatio) {
        Modifier
            .fillMaxWidth()
            .aspectRatio(RATIO_COMIC)
    } else {
        Modifier
            .fillMaxWidth()
            .aspectRatio(RATIO_COMMON)
    }
    val state = painter.state

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier.placeholder(
            visible = state is ImagePainter.State.Loading,
            highlight = PlaceholderHighlight.shimmer()
        ),
    )
}

/**
 * 角色卡面
 * 通过设置 aspectRatio, 使图片加载前时，有默认高度
 */
@ExperimentalCoilApi
@Composable
fun CharacterCardImage(
    url: String,
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit = {}
) {
    val painter = rememberImagePainter(
        data = url,
        builder = {
            crossfade(Constants.FADE_TIME)
            listener(onSuccess = { _, _ ->
                onSuccess.invoke()
            })
        })

    val state = painter.state
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(RATIO)
            .placeholder(
                visible = state is ImagePainter.State.Loading,
                highlight = PlaceholderHighlight.shimmer()
            ),
        contentScale = ContentScale.FillWidth
    )
}


/**
 * 角色位置图标
 */
@Composable
fun PositionIcon(position: Int) {
    val positionIconId = when (position) {
        in 0..299 -> R.drawable.ic_position_0
        in 300..599 -> R.drawable.ic_position_1
        in 600..9999 -> R.drawable.ic_position_2
        else -> R.drawable.ic_position_2
    }
    Image(
        painter = rememberImagePainter(
            data = positionIconId,
        ),
        contentDescription = null,
        modifier = Modifier.size(Dimen.smallIconSize)
    )
}

/**
 * 图标
 */
@ExperimentalCoilApi
@Composable
fun IconCompose(
    data: Any,
    size: Dp = Dimen.iconSize,
    tint: Color = MaterialTheme.colors.primary,
    wrapSize: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var mModifier = if (onClick != null) {
        Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick.invoke()
            })
    } else {
        Modifier.clip(MaterialTheme.shapes.small)
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
        val painter = rememberImagePainter(
            data = data,
            builder = {
                crossfade(Constants.FADE_TIME)
                error(R.drawable.unknown_gray)
            }
        )
        val state = painter.state
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = mModifier.placeholder(
                visible = state is ImagePainter.State.Loading,
                highlight = PlaceholderHighlight.shimmer()
            )
        )
    }
}

/**
 * 获取装备图标链接
 */
fun getEquipIconUrl(id: Int) = Constants.EQUIPMENT_URL + id + Constants.WEBP