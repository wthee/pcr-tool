package cn.wthee.pcrtool.ui.common

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.VibrateUtil
import coil.compose.rememberImagePainter
import coil.drawable.ScaleDrawable

const val RATIO = 1.78f

// 741 * 1200
const val RATIO_COMIC = 0.6175f
const val RATIO_COMMON = 371 / 208f


@Composable
fun ImageCompose(
    data: Any,
    ratio: Float,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current

    val painter = rememberImagePainter(
        data = data,
        builder = {
            placeholder(
                ScaleDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.load)!!
                )
            )
            error(R.drawable.error)
            listener(onSuccess = { _, _ ->
                onSuccess.invoke()
            })
        })

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .aspectRatio(ratio),
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
    Image(
        painter = rememberImagePainter(
            data = positionIconId,
        ),
        contentDescription = null,
        modifier = modifier.size(size)
    )
}

/**
 * 图标
 */
@Composable
fun IconCompose(
    data: Any,
    size: Dp = Dimen.iconSize,
    tint: Color = MaterialTheme.colorScheme.primary,
    wrapSize: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var mModifier = if (onClick != null) {
        Modifier
            .clip(Shape.small)
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick.invoke()
            })
    } else {
        Modifier.clip(Shape.small)
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
                placeholder(R.drawable.unknown_gray)
                error(R.drawable.unknown_gray)
            }
        )
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = mModifier
        )
    }
}

/**
 * 获取装备图标链接
 */
fun getEquipIconUrl(id: Int) = Constants.EQUIPMENT_URL + id + Constants.WEBP