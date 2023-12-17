package cn.wthee.pcrtool.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.launch

/**
 * 通用悬浮按钮
 *
 * @param hasNavBarPadding 适配导航栏
 * @param extraContent 不为空时，将替换text内容
 * @param iconScale 图标缩放，非ImageVector才生效
 */
@Composable
fun MainSmallFab(
    iconType: Any,
    modifier: Modifier = Modifier,
    text: String = "",
    hasNavBarPadding: Boolean = true,
    extraContent: (@Composable () -> Unit)? = null,
    iconScale: ContentScale = ContentScale.FillWidth,
    vibrate: Boolean = true,
    onClick: () -> Unit= {}
) {
    val context = LocalContext.current
    var mModifier = if (hasNavBarPadding) {
        modifier.navigationBarsPadding()
    } else {
        modifier
    }
    val isTextFab = text != "" && extraContent == null

    if (isTextFab) {
        mModifier = mModifier.padding(horizontal = Dimen.textFabMargin)
    }

    SmallFloatingActionButton(
        onClick = {
            if (vibrate) {
                VibrateUtil(context).single()
            }
            onClick()
        },
        shape = CircleShape,
        modifier = mModifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = (if (isTextFab) {
                Modifier.padding(start = Dimen.largePadding)
            } else {
                Modifier.padding(start = 0.dp)
            }).animateContentSize(defaultTween())
        ) {

            if (extraContent == null) {
                MainIcon(
                    data = iconType,
                    size = Dimen.fabIconSize,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    contentScale = iconScale
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    modifier = if (isTextFab) {
                        Modifier.padding(start = Dimen.mediumPadding, end = Dimen.largePadding)
                    } else {
                        Modifier
                    }
                        .widthIn(max = Dimen.fabTextMaxWidth),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                extraContent()
            }
        }
    }

}

/**
 * 切换
 * @param width 宽度
 */
@Composable
fun SelectTypeFab(
    modifier: Modifier = Modifier,
    openDialog: Boolean,
    changeDialog: (Boolean) -> Unit,
    icon: MainIconType,
    tabs: List<String>,
    type: Int,
    width: Dp = Dimen.dataChangeWidth,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    paddingValues: PaddingValues = PaddingValues(
        start = Dimen.fabMargin,
        end = Dimen.fabMarginEnd,
        top = Dimen.fabMargin,
        bottom = Dimen.fabMargin,
    ),
    changeSelect: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    //切换
    SmallFloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(paddingValues)
            .padding(
                start = Dimen.mediumPadding,
                end = Dimen.textFabMargin,
                top = Dimen.mediumPadding,
            )
            .navigationBarsPadding(),
        shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                changeDialog(true)
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (openDialog) {
                Dimen.popupMenuElevation
            } else {
                Dimen.fabElevation
            }
        ),
    ) {
        if (openDialog) {
            Column(
                modifier = Modifier.widthIn(max = width),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //选择
                tabs.forEachIndexed { index, tab ->
                    SelectText(
                        selected = type == index,
                        text = tab,
                        textStyle = MaterialTheme.typography.titleLarge,
                        selectedColor = selectedColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimen.mediumPadding)
                    ) {
                        coroutineScope.launch {
                            changeSelect(index)
                        }
                        changeDialog(false)
                    }
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                MainIcon(
                    data = icon, tint = selectedColor,
                    size = Dimen.fabIconSize
                )
                Text(
                    text = tabs[type],
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = selectedColor,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding, end = Dimen.largePadding
                    )
                )
            }

        }
    }
}


@CombinedPreviews
@Composable
private fun FabComposePreview() {
    PreviewLayout {
        Row {
            MainSmallFab(iconType = MainIconType.ANIMATION) {

            }
            MainSmallFab(iconType = MainIconType.ANIMATION, text = "fab") {

            }
        }
    }
}
