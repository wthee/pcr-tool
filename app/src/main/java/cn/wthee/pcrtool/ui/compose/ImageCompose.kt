package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
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
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.MOVE_SPEED_RATIO
import com.google.accompanist.coil.rememberCoilPainter

//fixme previewPlaceholder 无效

/**
 * 角色卡面
 * 通过设置 aspectRatio, 使图片加载前时，有默认高度
 */
@Composable
fun CharacterCard(url: String, clip: Boolean = false, scrollState: ScrollState? = null) {
    val modifier = if (scrollState == null) {
        if (clip) {
            CharacterCardImageModifier.clip(CardTopShape)
        } else {
            CharacterCardImageModifier
        }
    } else {
        //滑动时，向上平移
        val move = ((-scrollState.value) * MOVE_SPEED_RATIO).dp
        if (clip) {
            CharacterCardImageModifier
                .clip(CardTopShape)
                .offset(y = move)
        } else {
            CharacterCardImageModifier.offset(y = move)
        }
    }
    Image(
        painter = rememberCoilPainter(
            request = url,
            previewPlaceholder = R.drawable.load,
            fadeIn = true,
            fadeInDurationMs = 600
        ),
        contentDescription = null,
        modifier = modifier
    )
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
            previewPlaceholder = R.drawable.unknown_gray,
        ),
        contentDescription = null,
        modifier = Modifier.size(size)
    )
}

/**
 * 图标
 */
@Composable
fun IconCompose(data: Any, modifier: Modifier = Modifier) {
    Image(
        painter = rememberCoilPainter(
            request = data,
            previewPlaceholder = R.drawable.unknown_gray,
        ),
        contentDescription = null,
        modifier = modifier.size(Dimen.iconSize)
    )
}

/**
 * 获取装备图标链接
 */
fun getEquipIconUrl(id: Int) = Constants.EQUIPMENT_URL + id + Constants.WEBP
