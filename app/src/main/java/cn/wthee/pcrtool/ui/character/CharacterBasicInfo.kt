package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.copyText
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

/**
 * 角色基本信息
 *
 * @param unitId 角色编号
 */
@Composable
fun CharacterBasicInfo(
    unitId: Int,
    viewModel: CharacterViewModel = hiltViewModel()
) {
    val data = viewModel.getCharacter(unitId).collectAsState(initial = null).value
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        data?.let { info ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                BasicInfo(info = info)
                HomePageCommentInfo(info.getSelf(), info.getCommentList())
                RoomComment(unitId = unitId, viewModel = viewModel)
            }
        }
    }

}

/**
 * 角色基本信息
 */
@Composable
private fun BasicInfo(info: CharacterInfoPro) {
    Column(
        modifier = Modifier
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
            .fillMaxWidth()
    ) {
        //标题
        MainText(
            text = info.catchCopy.deleteSpace,
            modifier = Modifier
                .padding(Dimen.largePadding)
                .fillMaxWidth(),
            selectable = true
        )
        //介绍
        Subtitle2(
            text = info.getIntroText()
        )
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.character),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(text = info.name, modifier = Modifier.weight(0.85f), selectable = true)
        }
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.name),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = info.actualName,
                modifier = Modifier.weight(0.85f),
                selectable = true
            )
        }
        //身高、体重
        TwoColumnsInfo(
            stringResource(id = R.string.title_height),
            info.getFixedHeight() + " CM",
            stringResource(id = R.string.title_weight),
            info.getFixedWeight() + " KG"
        )
        //生日、年龄
        TwoColumnsInfo(
            stringResource(id = R.string.title_birth),
            stringResource(id = R.string.date_m_d, info.birthMonth, info.birthDay),
            stringResource(id = R.string.age),
            info.getFixedAge()
        )
        //血型、位置
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.35f)
            ) {
                PositionIcon(position = info.position)
                MainContentText(
                    text = info.position.toString(),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
        }
        //种族、cv
        TwoColumnsInfo(
            stringResource(id = R.string.title_race),
            info.race,
            stringResource(id = R.string.cv),
            info.voice
        )
        //公会
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_guild),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.guild,
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
        //兴趣
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
    }
}

/**
 * 主页交流信息
 */
@Composable
private fun HomePageCommentInfo(selfText: String?, commentList: List<String>) {
    //介绍信息
    selfText?.let {
        Row(modifier = Modifier.padding(start = Dimen.largePadding, top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_self),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        CommentTextCard(text = it)
    }

    Row(modifier = Modifier.padding(start = Dimen.largePadding, top = Dimen.mediumPadding)) {
        MainTitleText(
            text = stringResource(id = R.string.title_comments),
            modifier = Modifier.weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.85f))
    }
    commentList.forEach {
        CommentTextCard(it)
    }
}

/**
 * 两列信息
 */
@Composable
private fun TwoColumnsInfo(
    title0: String,
    text0: String,
    title1: String,
    text1: String
) {
    Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
        MainTitleText(
            text = title0,
            modifier = Modifier.weight(0.15f)
        )
        MainContentText(
            text = text0,
            modifier = Modifier
                .weight(0.35f)
                .padding(end = Dimen.mediumPadding),
            selectable = true
        )
        MainTitleText(
            text = title1,
            modifier = Modifier.weight(0.15f)
        )
        MainContentText(
            text = text1,
            modifier = Modifier.weight(0.35f),
            selectable = true
        )
    }
}

/**
 * 小屋交流文本
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun RoomComment(unitId: Int, viewModel: CharacterViewModel) {
    val roomComments =
        viewModel.getRoomComments(unitId).collectAsState(initial = null).value
    val pagerState = rememberPagerState()


    Row(modifier = Modifier.padding(start = Dimen.largePadding, top = Dimen.mediumPadding)) {
        MainTitleText(
            text = stringResource(id = R.string.title_room_comments),
            modifier = Modifier.weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.85f))
    }
    roomComments?.let {
        //多角色时，显示角色图标
        if (roomComments.size > 1) {
            val urls = arrayListOf<String>()
            roomComments.forEach { roomComment ->
                urls.add(
                    ImageResourceHelper.getInstance().getMaxIconUrl(roomComment.unitId)
                )
            }
            IconHorizontalPagerIndicator(pagerState, urls)
        }
        HorizontalPager(
            state = pagerState,
            count = roomComments.size,
            verticalAlignment = Alignment.Top
        ) { index ->
            Column {
                roomComments[index].getCommentList().forEach {
                    CommentTextCard(it)
                }
                CommonSpacer()
            }
        }
    }
}

/**
 * 文本卡片
 */
@Composable
private fun CommentTextCard(text: String) {
    val context = LocalContext.current
    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        ),
        onClick = {
            copyText(context, text)
        }
    ) {
        MainContentText(
            text = text,
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
    }
}