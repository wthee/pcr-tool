package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.CharacterCardImageModifier
import cn.wthee.pcrtool.utils.Constants
import coil.Coil
import com.google.accompanist.coil.CoilImage

/**
 * 角色卡面
 * 通过设置 aspectRatio, 使图片加载前时，有默认高度
 */
@Composable
fun CharacterCard(url: String, clip: Boolean = false) {
    val modifier = if (clip) {
        CharacterCardImageModifier.clip(CardTopShape)
    } else {
        CharacterCardImageModifier
    }
    CoilImage(
        data = url,
        contentDescription = null,
        modifier = modifier,
        imageLoader = Coil.imageLoader(MyApplication.context),
        loading = {
            CoilImage(data = R.drawable.load, contentDescription = null)
        },
        error = {
            CoilImage(data = R.drawable.error, contentDescription = null)
        })
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
    CoilImage(data = positionIconId, contentDescription = null, modifier = Modifier.size(size))
}

/**
 * 图标
 */
@Composable
fun IconCompose(data: Any, modifier: Modifier = Modifier) {
    CoilImage(
        data = data,
        contentDescription = null,
        modifier = modifier,
        loading = {
            CoilImage(data = R.drawable.unknown_gray, contentDescription = null)
        },
        error = {
            CoilImage(data = R.drawable.unknown_gray, contentDescription = null)
        }
    )
}

/**
 * 获取装备图标链接
 */
fun getEquipIconUrl(id: Int) = Constants.EQUIPMENT_URL + id + Constants.WEBP
