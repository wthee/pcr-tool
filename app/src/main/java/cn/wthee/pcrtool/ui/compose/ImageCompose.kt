package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.CharacterCardImageModifier
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.MOVE_SPEED_RATIO
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState

/**
 * 角色卡面
 * 通过设置 aspectRatio, 使图片加载前时，有默认高度
 */
@Composable
fun CharacterCard(
    url: String,
    clip: Boolean = false,
    scrollState: ScrollState? = null
) {
    val modifier = CharacterCardImageModifier
    if (scrollState != null) {
        //滑动时，向上平移
        val move = ((-scrollState.value) * MOVE_SPEED_RATIO).dp
        modifier.offset(y = move)
    }
    if (clip) {
        modifier.clip(CardTopShape)
    }
    Box {
        val painter = rememberCoilPainter(request = url)
        Image(
            painter = when (painter.loadState) {
                is ImageLoadState.Success -> painter
                is ImageLoadState.Error -> rememberCoilPainter(request = R.drawable.error)
                else -> rememberCoilPainter(request = R.drawable.load)
            },
            contentDescription = null,
            modifier = modifier
        )
    }
}


/**
 * 角色位置图标
 */
@Composable
fun PositionIcon(position: Int, size: Dp) {
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
        modifier = Modifier.size(size)
    )
}

/**
 * 图标
 */
@Composable
fun IconCompose(data: Any, modifier: Modifier = Modifier, size: Dp = Dimen.iconSize) {
    Box {
        val painter = rememberCoilPainter(request = data)
        Image(
            painter = when (painter.loadState) {
                is ImageLoadState.Success -> painter
                is ImageLoadState.Error -> rememberCoilPainter(request = Constants.UNKNOWN_EQUIPMENT_ICON)
                else -> rememberCoilPainter(request = R.drawable.unknown_gray)
            },
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(Shapes.small)

        )
    }
}

/**
 * 获取装备图标链接
 */
fun getEquipIconUrl(id: Int) = Constants.EQUIPMENT_URL + id + Constants.WEBP
