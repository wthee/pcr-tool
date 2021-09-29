package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

/**
 * 角色基本信息
 *
 * @param unitId 角色编号
 */
@ExperimentalAnimationApi
@Composable
fun CharacterBasicInfo(
    scrollState: ScrollState,
    unitId: Int,
    viewModel: CharacterViewModel = hiltViewModel()
) {
    val data = viewModel.getCharacter(unitId).collectAsState(initial = null).value

    Box(modifier = Modifier.fillMaxSize()) {
        data?.let { info ->
            CharacterInfoCompose(info, scrollState)
        }
    }

}

@Composable
private fun CharacterInfoCompose(info: CharacterInfoPro, scrollState: ScrollState) {
    Column(
        modifier = Modifier
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        //标题
        MainText(
            text = info.catchCopy.deleteSpace,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(Dimen.largePadding),
            selectable = true
        )
        //介绍
        Text(
            info.getIntroText(),
            style = MaterialTheme.typography.subtitle2,
        )
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.character),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(text = info.name, modifier = Modifier.weight(0.85f))
        }
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.name),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(text = info.actualName, modifier = Modifier.weight(0.85f))
        }
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_height),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = info.getFixedHeight() + " CM",
                modifier = Modifier
                    .weight(0.35f)
                    .padding(end = Dimen.mediumPadding)
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
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_birth),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = stringResource(
                    id = R.string.date_m_d,
                    info.birthMonth,
                    info.birthDay
                ),
                modifier = Modifier
                    .weight(0.35f)
                    .padding(end = Dimen.mediumPadding)
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
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_blood),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = info.bloodType,
                modifier = Modifier
                    .weight(0.35f)
                    .padding(end = Dimen.mediumPadding)
            )
            MainTitleText(
                text = stringResource(id = R.string.title_position),
                modifier = Modifier.weight(0.15f)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(0.35f)
            ) {
                PositionIcon(position = info.position)
                MainContentText(
                    text = info.position.toString(),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
        }
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_race),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = info.race,
                modifier = Modifier
                    .weight(0.35f)
                    .padding(end = Dimen.mediumPadding),
                selectable = true
            )
            MainTitleText(
                text = stringResource(id = R.string.cv),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = info.voice,
                modifier = Modifier.weight(0.35f),
                selectable = true
            )
        }
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_guild),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.guild,
            modifier = Modifier.padding(
                top = Dimen.mediumPadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding
            ),
            textAlign = TextAlign.Start,
            selectable = true
        )
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_fav),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.favorite,
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
        info.getSelf()?.let {
            Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
                MainTitleText(
                    text = stringResource(id = R.string.title_self),
                    modifier = Modifier.weight(0.15f)
                )
                Spacer(modifier = Modifier.weight(0.85f))
            }
            MainContentText(
                text = it,
                modifier = Modifier.padding(Dimen.mediumPadding),
                textAlign = TextAlign.Start,
                selectable = true
            )
        }
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_comments),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.getCommentsText(),
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_room_comments),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.getRoomCommentsText(),
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
        CommonSpacer()
    }
}

@Preview
@Composable
private fun Preview() {
    val scrollState = rememberScrollState()
    PreviewBox(1) {
        CharacterInfoCompose(info = CharacterInfoPro(), scrollState = scrollState)
    }
}

@Preview
@Composable
private fun PreviewDark() {
    val scrollState = rememberScrollState()
    PreviewBox(2) {
        CharacterInfoCompose(info = CharacterInfoPro(), scrollState = scrollState)
    }
}

