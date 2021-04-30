package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.character.*
import cn.wthee.pcrtool.ui.equip.EquipList
import cn.wthee.pcrtool.ui.equip.EquipMainInfo
import cn.wthee.pcrtool.ui.home.CharacterList
import cn.wthee.pcrtool.ui.tool.EventList
import cn.wthee.pcrtool.ui.tool.GachaList
import cn.wthee.pcrtool.ui.tool.LeaderboardList
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

object Navigation {
    const val CHARACTER_LIST = "characterList"
    const val CHARACTER_DETAIL = "characterDetail"
    const val CHARACTER_BASIC_INFO = "characterBasicInfo"
    const val UNIT_ID = "unitId"
    const val UNIT_SIX_ID = "r6Id"
    const val EQUIP_LIST = "equipList"
    const val EQUIP_ID = "equipId"
    const val EQUIP_DETAIL = "equipDetail"
    const val RANK_EQUIP = "rankEquip"
    const val RANK_COMPARE = "rankCompare"
    const val MAX_RANK = "maxRank"
    const val LEVEL = "level"
    const val RARITY = "rarity"
    const val UNIQUE_EQUIP_LEVEL = "uniqueEquipLevel"
    const val EQUIP_COUNT = "equipCount"

    //工具
    const val TOOL_LEADER = "toolLeader"
    const val TOOL_GACHA = "toolGacha"
    const val TOOL_EVENT = "toolEvent"
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NavGraph(navController: NavHostController, viewModel: NavViewModel, actions: NavActions) {

    NavHost(navController, startDestination = Navigation.CHARACTER_LIST) {
        //首页
        composable(Navigation.CHARACTER_LIST) {
            CharacterList(
                actions.toCharacterDetail,
                viewModel
            )
        }

        //角色属性详情
        composable(
            "${Navigation.CHARACTER_DETAIL}/{${Navigation.UNIT_ID}}/{${Navigation.UNIT_SIX_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.UNIT_SIX_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            CharacterMainInfo(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                r6Id = arguments.getInt(Navigation.UNIT_SIX_ID),
                actions.toEquipDetail,
                actions.toCharacterBasicInfo,
                actions.toCharacteRankEquip,
                actions.toCharacteRankCompare,
                actions.toCharacteEquipCount,
                viewModel
            )
        }

        //角色资料
        composable(
            "${Navigation.CHARACTER_BASIC_INFO}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            CharacterBasicInfo(
                unitId = arguments.getInt(Navigation.UNIT_ID)
            )
        }

        //装备列表
        composable(Navigation.EQUIP_LIST) {
            EquipList(viewModel, toEquipDetail = actions.toEquipDetail)
        }

        //装备详情
        composable(
            "${Navigation.EQUIP_DETAIL}/{${Navigation.EQUIP_ID}}",
            arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            EquipMainInfo(arguments.getInt(Navigation.EQUIP_ID))
        }

        //角色 RANK 装备
        composable(
            "${Navigation.RANK_EQUIP}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            RankEquipList(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                toEquipDetail = actions.toEquipDetail,
                navViewModel = viewModel
            )
        }

        //角色 RANK 对比
        composable(
            "${Navigation.RANK_COMPARE}/{${Navigation.UNIT_ID}}/{${Navigation.MAX_RANK}}/{${Navigation.LEVEL}}/{${Navigation.RARITY}}/{${Navigation.UNIQUE_EQUIP_LEVEL}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.MAX_RANK) {
                type = NavType.IntType
            }, navArgument(Navigation.LEVEL) {
                type = NavType.IntType
            }, navArgument(Navigation.RARITY) {
                type = NavType.IntType
            }, navArgument(Navigation.UNIQUE_EQUIP_LEVEL) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            RankCompare(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                maxRank = arguments.getInt(Navigation.MAX_RANK),
                level = arguments.getInt(Navigation.LEVEL),
                rarity = arguments.getInt(Navigation.RARITY),
                uniqueEquipLevel = arguments.getInt(Navigation.UNIQUE_EQUIP_LEVEL),
                navViewModel = viewModel
            )
        }

        //角色装备统计
        composable(
            "${Navigation.EQUIP_COUNT}/{${Navigation.UNIT_ID}}/{${Navigation.MAX_RANK}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.MAX_RANK) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            RankEquipCount(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                maxRank = arguments.getInt(Navigation.MAX_RANK),
                actions.toEquipDetail,
                navViewModel = viewModel
            )
        }

        //角色排行
        composable(Navigation.TOOL_LEADER) {
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            LeaderboardList()
        }

        //角色排行
        composable(Navigation.TOOL_GACHA) {
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            GachaList(actions.toCharacterDetail)
        }

        //剧情活动
        composable(Navigation.TOOL_EVENT) {
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            EventList(actions.toCharacterDetail)
        }
    }
}

/**
 * 导航
 */
class NavActions(navController: NavHostController) {
    /**
     * 角色详情
     */
    val toCharacterDetail: (Int, Int) -> Unit = { unitId: Int, r6Id: Int ->
        navController.navigate("${Navigation.CHARACTER_DETAIL}/${unitId}/${r6Id}")
    }

    /**
     * 装备列表
     */
    val toEquipList: () -> Unit = {
        navController.navigate(Navigation.EQUIP_LIST)
    }

    /**
     * 装备详情
     */
    val toEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${Navigation.EQUIP_DETAIL}/${equipId}")
    }

    /**
     * 角色资料
     */
    val toCharacterBasicInfo: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.CHARACTER_BASIC_INFO}/${unitId}")
    }

    /**
     * 角色 RANK 装备
     */
    val toCharacteRankEquip: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.RANK_EQUIP}/${unitId}")
    }

    /**
     * 角色 RANK 对比
     */
    val toCharacteRankCompare: (Int, Int, Int, Int, Int) -> Unit =
        { unitId: Int, maxRank: Int, level: Int, rarity: Int, uniqueEquipLevel: Int ->
            navController.navigate("${Navigation.RANK_COMPARE}/${unitId}/${maxRank}/${level}/${rarity}/${uniqueEquipLevel}")
        }

    /**
     * 角装备统计
     */
    val toCharacteEquipCount: (Int, Int) -> Unit =
        { unitId: Int, maxRank: Int ->
            navController.navigate("${Navigation.EQUIP_COUNT}/${unitId}/${maxRank}")
        }


    /**
     * 角色排行
     */
    val toLeaderboard = {
        navController.navigate(Navigation.TOOL_LEADER)
    }

    /**
     * 角色卡池
     */
    val toGacha = {
        navController.navigate(Navigation.TOOL_GACHA)
    }

    /**
     * 剧情活动
     */
    val toEventStory = {
        navController.navigate(Navigation.TOOL_EVENT)
    }

}

@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {


    /**
     * fab 图标显示
     */
    val fabMainIcon = MutableLiveData(R.drawable.ic_function)

    /**
     * 确认
     */
    val fabOK = MutableLiveData(false)

    /**
     * 关闭
     */
    val fabClose = MutableLiveData(false)

    /**
     * 选择的 RANK
     */
    val selectRank = MutableLiveData(0)


    /**
     * 下载状态
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     */
    val downloadProgress = MutableLiveData(-2)

    /**
     * 加载中
     */
    val loading = MutableLiveData(false)

}
