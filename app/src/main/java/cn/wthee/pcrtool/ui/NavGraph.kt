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
import cn.wthee.pcrtool.ui.character.CharacterPager
import cn.wthee.pcrtool.ui.equip.EquipList
import cn.wthee.pcrtool.ui.equip.EquipMainInfo
import cn.wthee.pcrtool.ui.home.CharacterList
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

object Navigation {
    const val CHARACTER_LIST = "characterList"
    const val CHARACTER_DETAIL = "characterDetail"
    const val UNIT_ID = "unitId"
    const val UNIT_SIX_ID = "r6Id"
    const val EQUIP_LIST = "equipList"
    const val EQUIP_ID = "equipId"
    const val EQUIP_DETAIL = "equipDetail"
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

        //角色详情
        composable(
            "${Navigation.CHARACTER_DETAIL}/{${Navigation.UNIT_ID}}/{${Navigation.UNIT_SIX_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.UNIT_SIX_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.pageLevel.postValue(1)
            CharacterPager(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                r6Id = arguments.getInt(Navigation.UNIT_SIX_ID),
                actions.toEquipDetail
            )
        }

        //装备列表
        composable(Navigation.EQUIP_LIST) {
            viewModel.pageLevel.postValue(1)
            EquipList(toEquipDetail = actions.toEquipDetail)
        }

        //装备详情
        composable(
            "${Navigation.EQUIP_DETAIL}/{${Navigation.EQUIP_ID}}",
            arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.pageLevel.postValue(2)
            EquipMainInfo(arguments.getInt(Navigation.EQUIP_ID))
        }
    }
}

/**
 * 导航
 */
class NavActions(navController: NavHostController) {
    val toCharacterDetail: (Int, Int) -> Unit = { unitId: Int, r6Id: Int ->
        navController.navigate("${Navigation.CHARACTER_DETAIL}/${unitId}/${r6Id}")
    }

    val toEquipList: () -> Unit = {
        navController.navigate(Navigation.EQUIP_LIST)
    }

    val toEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${Navigation.EQUIP_DETAIL}/${equipId}")
    }
}

@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {
    /**
     * 页面等级
     */
    val pageLevel = MutableLiveData(0)

    /**
     * fab 显示
     */
    val fabShow = MutableLiveData(true)

    /**
     * 下载状态
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     */
    val downloadProgress = MutableLiveData(-2)
}
