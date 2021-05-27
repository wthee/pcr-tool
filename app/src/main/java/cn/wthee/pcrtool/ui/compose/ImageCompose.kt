package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.MOVE_SPEED_RATIO
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.vibrate
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState

/**
 * 角色卡面
 * 通过设置 aspectRatio, 使图片加载前时，有默认高度
 */
@Composable
fun CharacterCard(
    url: String,
    scrollState: ScrollState? = null
) {

    val modifier = Modifier
        .aspectRatio(Constants.RATIO)
        .fillMaxWidth()
    if (scrollState != null) {
        //滑动时，向上平移
        val move = ((-scrollState.value) * MOVE_SPEED_RATIO).dp
        modifier.offset(y = move)
    }
    val painter = rememberCoilPainter(request = url)

    Image(
        painter = when (painter.loadState) {
            is ImageLoadState.Success -> painter
            is ImageLoadState.Loading -> rememberCoilPainter(request = R.drawable.load)
            else -> rememberCoilPainter(request = R.drawable.error)
        },
        contentDescription = null,
        modifier = modifier
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
        painter = rememberCoilPainter(
            request = positionIconId,
        ),
        contentDescription = null,
        modifier = Modifier.size(Dimen.smallIconSize)
    )
}

/**
 * 图标
 */
@Composable
fun IconCompose(
    data: Any,
    size: Dp = Dimen.iconSize,
    tint: Color = MaterialTheme.colors.primary,
    fade: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val mModifier = if (onClick != null) {
        Modifier
            .clip(Shapes.small)
            .clickable(onClick = onClick.vibrate {
                VibrateUtil(context).single()
            })
            .size(size)
    } else {
        Modifier
            .clip(Shapes.small)
            .size(size)
    }

    if (data is ImageVector) {
        Icon(
            imageVector = data,
            contentDescription = null,
            tint = tint,
            modifier = mModifier
        )
    } else {
        val painter = rememberCoilPainter(request = data, fadeIn = fade, fadeInDurationMs = 400)
        Image(
            painter = when (painter.loadState) {
                is ImageLoadState.Success -> painter
                is ImageLoadState.Error -> rememberCoilPainter(request = R.drawable.unknown_gray)
                else -> rememberCoilPainter(request = R.drawable.unknown_gray)
            },
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = mModifier
        )
    }
}

/**
 * 获取装备图标链接
 */
fun getEquipIconUrl(id: Int) = Constants.EQUIPMENT_URL + id + Constants.WEBP
