package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterHomePageComment
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.*
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
    val scrollState = rememberScrollState()
    val data = viewModel.getCharacter(unitId).collectAsState(initial = null).value
    val homePageCommentList =
        viewModel.getHomePageComments(unitId).collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        data?.let { info ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicInfo(info = info)
                HomePageCommentInfo(info.getSelf(), homePageCommentList)
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
            text = info.getIntroText(),
            selectable = true
        )
        //角色
        SingleRow(title = stringResource(id = R.string.character), content = info.unitName)
        //现实名字
        SingleRow(title = stringResource(id = R.string.name), content = info.actualName.fixedStr)
        //cv
        SingleRow(title = stringResource(id = R.string.cv), content = info.voice)

        //身高、体重
        TwoColumnsInfo(
            stringResource(id = R.string.title_height),
            "${info.height.fixedStr} CM",
            stringResource(id = R.string.title_weight),
            "${info.weight.fixedStr} KG"
        )
        //生日、年龄
        TwoColumnsInfo(
            stringResource(id = R.string.title_birth),
            stringResource(
                id = R.string.date_m_d,
                info.birthMonth.fixedStr,
                info.birthDay.fixedStr
            ),
            stringResource(id = R.string.age),
            info.age.fixedStr
        )
        //血型、种族
        TwoColumnsInfo(
            stringResource(id = R.string.title_blood),
            info.bloodType.fixedStr,
            stringResource(id = R.string.title_race),
            info.race,
        )
        //公会
        TwoRow(title = stringResource(id = R.string.title_guild), content = info.guild)
        //兴趣
        TwoRow(title = stringResource(id = R.string.title_fav), content = info.favorite)
    }
}

/**
 * 主页交流信息
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun HomePageCommentInfo(
    selfText: String?,
    homePageCommentList: List<CharacterHomePageComment>
) {
    //介绍信息
    selfText?.let {
        Row(modifier = Modifier.padding(start = Dimen.largePadding, top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_self),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        CommentText(text = it)
    }

    //主页交流
    val pagerState = rememberPagerState()

    Row(
        modifier = Modifier.padding(
            start = Dimen.largePadding,
            top = Dimen.mediumPadding,
            bottom = Dimen.mediumPadding
        )
    ) {
        MainTitleText(
            text = stringResource(id = R.string.title_comments),
            modifier = Modifier.weight(0.15f)
        )
        MainTitleText(
            text = stringResource(id = R.string.title_home_page_comments),
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.7f))
    }
    //多星级时
    if (homePageCommentList.isNotEmpty()) {
        val tabs = arrayListOf<String>()
        homePageCommentList.forEach {
            tabs.add(
                "★" + if (it.unitId % 100 / 10 == 0) {
                    "1"
                } else {
                    "${it.unitId % 100 / 10}"
                }
            )
        }
        MainTabRow(
            pagerState = pagerState,
            tabs = tabs,
            modifier = Modifier
                .padding(horizontal = Dimen.mediumPadding)
                .fillMaxWidth(homePageCommentList.size * 0.33f)
        )

        HorizontalPager(
            state = pagerState,
            count = homePageCommentList.size,
            verticalAlignment = Alignment.Top
        ) { index ->
            Column {
                homePageCommentList[index].getCommentList().forEach {
                    CommentText(it)
                }
            }
        }
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


    Row(
        modifier = Modifier.padding(
            start = Dimen.largePadding,
            top = Dimen.mediumPadding,
            bottom = Dimen.mediumPadding
        )
    ) {
        MainTitleText(
            text = stringResource(id = R.string.title_comments),
            modifier = Modifier.weight(0.15f)
        )
        MainTitleText(
            text = stringResource(id = R.string.title_room_comments),
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.7f))
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
                    CommentText(it)
                }
                CommonSpacer()
            }
        }
    }
}

/**
 * 单行
 */
@Composable
private fun SingleRow(title: String, content: String) {
    Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
        MainTitleText(
            text = title,
            modifier = Modifier.weight(0.15f)
        )
        MainContentText(
            text = content,
            modifier = Modifier.weight(0.85f),
            selectable = true
        )
    }
}

/**
 * 双行显示
 */
@Composable
private fun TwoRow(title: String, content: String) {
    Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
        MainTitleText(
            text = title,
            modifier = Modifier.weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.85f))
    }
    MainContentText(
        text = content,
        modifier = Modifier.padding(Dimen.mediumPadding),
        textAlign = TextAlign.Start,
        selectable = true
    )
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
 * 文本
 */
@Composable
private fun CommentText(text: String) {
    val context = LocalContext.current
    MainContentText(
        text = text,
        modifier = Modifier
            .padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.smallPadding
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                VibrateUtil(context).single()
                copyText(context, text)
            }
            .padding(Dimen.smallPadding),
        textAlign = TextAlign.Start,
    )
}

/**
 * 角色基本信息预览
 */
@CombinedPreviews
@Composable
private fun BasicInfoPreview() {
    PreviewLayout {
        BasicInfo(info = CharacterInfoPro())
    }
}