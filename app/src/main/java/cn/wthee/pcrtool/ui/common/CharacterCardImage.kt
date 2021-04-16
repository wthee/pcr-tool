package cn.wthee.pcrtool.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.CharacterCardImageModifier
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