package cn.wthee.pcrtool.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import kotlinx.coroutines.launch

/**
 * 菜单
 */
@Composable
fun MenuContent(viewModel: NavViewModel, actions: NavActions) {
    val pageLevel = viewModel.pageLevel.observeAsState().value ?: 0
    val coroutineScope = rememberCoroutineScope()
    if (pageLevel == -1) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.alpha_black))
                .clickable {
                    viewModel.pageLevel.postValue(0)
                }
        ) {
            Row {
                MenuItem(
                    text = stringResource(id = R.string.tool_leader),
                    iconId = R.drawable.ic_leader,
                    modifier = Modifier
                        .weight(0.5f)
                        .height(Dimen.smallMenuHeight)
                ) {
                    actions.toLeaderboard()
                }
            }

            Row {
                MenuItem(
                    text = stringResource(id = R.string.tool_equip),
                    iconId = R.drawable.ic_equip,
                    modifier = Modifier
                        .weight(0.5f)
                        .height(Dimen.smallMenuHeight)
                ) {
                    actions.toEquipList()
                }
                MenuItem(
                    text = stringResource(id = R.string.setting),
                    iconId = R.drawable.ic_settings,
                    modifier = Modifier
                        .weight(0.5f)
                        .height(Dimen.smallMenuHeight)
                ) {

                }
            }
            Row(
                modifier = Modifier
                    .padding(
                        top = Dimen.mediuPadding,
                        bottom = Dimen.fabMargin,
                        end = Dimen.fabMarginEnd
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                //数据版本切换
                ExtendedFloatingActionButton(
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
                    modifier = Modifier.height(Dimen.fabSize),
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_change),
                            null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(Dimen.fabIconSize)
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.change_db),
                            style = MaterialTheme.typography.subtitle2,
                            color = MaterialTheme.colors.primary
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            viewModel.pageLevel.postValue(0)
                            DatabaseUpdater(viewModel).changeType()
                        }
                    }
                )
            }
        }
    }

}

/**
 * 菜单项
 */
@Composable
fun MenuItem(text: String, @DrawableRes iconId: Int, modifier: Modifier, action: () -> Unit) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = modifier
            .padding(Dimen.mediuPadding)
            .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
            .clickable(onClick = action)
    ) {
        Box() {
            Text(
                text = text,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .align(Alignment.TopStart)
            )
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}
