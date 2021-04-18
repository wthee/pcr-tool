package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.compose.PositionIcon
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

/**
 * 角色基本信息
 */
@Composable
fun CharacterBasicInfo(
    unitId: Int,
    r6Id: Int,
    viewModel: CharacterViewModel = hiltNavGraphViewModel()
) {
    viewModel.getCharacter(unitId)
    val data = viewModel.character.observeAsState().value

    data?.let { info ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            //标题
            Text(
                info.catchCopy,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.mediuPadding, bottom = Dimen.smallPadding)
            )
            //介绍
            Text(info.getIntroText(), modifier = Modifier.padding(Dimen.mediuPadding))
            Column {
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.character),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(text = info.name, modifier = Modifier.weight(0.85f))
                }
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.name),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(text = info.actualName, modifier = Modifier.weight(0.85f))
                }
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_height),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(
                        text = info.getFixedHeight() + " CM",
                        modifier = Modifier
                            .weight(0.35f)
                            .padding(end = Dimen.mediuPadding)
                    )
                    MainTitleText(
                        text = stringResource(id = R.string.title_weight),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(
                        text = info.getFixedWeight() + " KG",
                        modifier = Modifier.weight(0.35f)
                    )
                }
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_birth),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(
                        text = info.getBirth(),
                        modifier = Modifier
                            .weight(0.35f)
                            .padding(end = Dimen.mediuPadding)
                    )
                    MainTitleText(
                        text = stringResource(id = R.string.age),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(
                        text = info.getFixedAge(),
                        modifier = Modifier.weight(0.35f)
                    )
                }
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_blood),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(
                        text = info.bloodType,
                        modifier = Modifier
                            .weight(0.35f)
                            .padding(end = Dimen.mediuPadding)
                    )
                    MainTitleText(
                        text = stringResource(id = R.string.title_position),
                        modifier = Modifier.weight(0.15f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.weight(0.35f)
                    ) {
                        PositionIcon(position = info.position ?: 0, size = 20.dp)
                        MainContentText(
                            text = info.position.toString(),
                            modifier = Modifier.padding(start = Dimen.smallPadding)
                        )
                    }
                }
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_race),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(
                        text = info.race,
                        modifier = Modifier
                            .weight(0.35f)
                            .padding(end = Dimen.mediuPadding)
                    )
                    MainTitleText(
                        text = stringResource(id = R.string.cv),
                        modifier = Modifier.weight(0.15f)
                    )
                    MainContentText(
                        text = info.voice,
                        modifier = Modifier.weight(0.35f)
                    )
                }
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_guild),
                        modifier = Modifier.weight(0.15f)
                    )
                    Spacer(modifier = Modifier.weight(0.85f))
                }
                MainContentText(
                    text = info.guild,
                    modifier = Modifier.padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    ),
                    textAlign = TextAlign.Start
                )
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_fav),
                        modifier = Modifier.weight(0.15f)
                    )
                    Spacer(modifier = Modifier.weight(0.85f))
                }
                MainContentText(
                    text = info.favorite,
                    modifier = Modifier.padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    ),
                    textAlign = TextAlign.Start
                )
                info.getSelf()?.let {
                    Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                        MainTitleText(
                            text = stringResource(id = R.string.title_self),
                            modifier = Modifier.weight(0.15f)
                        )
                        Spacer(modifier = Modifier.weight(0.85f))
                    }
                    MainContentText(
                        text = it,
                        modifier = Modifier.padding(
                            top = Dimen.mediuPadding,
                            start = Dimen.largePadding,
                            end = Dimen.largePadding
                        ),
                        textAlign = TextAlign.Start
                    )
                }
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_comments),
                        modifier = Modifier.weight(0.15f)
                    )
                    Spacer(modifier = Modifier.weight(0.85f))
                }
                MainContentText(
                    text = info.getCommentsText(),
                    modifier = Modifier.padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    ),
                    textAlign = TextAlign.Start
                )
                Row(modifier = Modifier.padding(Dimen.mediuPadding)) {
                    MainTitleText(
                        text = stringResource(id = R.string.title_room_comments),
                        modifier = Modifier.weight(0.15f)
                    )
                    Spacer(modifier = Modifier.weight(0.85f))
                }
                MainContentText(
                    text = info.getRoomCommentsText(),
                    modifier = Modifier.padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    ),
                    textAlign = TextAlign.Start
                )
            }


        }
    }

}


