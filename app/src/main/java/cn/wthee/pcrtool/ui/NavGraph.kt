package cn.wthee.pcrtool.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import cn.wthee.pcrtool.ui.character.CharacterBasicInfo
import cn.wthee.pcrtool.ui.home.CharacterList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

object Navigation {
    const val CHARACTER_LIST = "characterList"
    const val CHARACTER_DETAIL = "characterDetail"
    const val UNIT_ID = "unitId"
    const val UNIT_SIX_ID = "r6Id"
}

@ExperimentalFoundationApi
@Composable
fun NavGraph(navController: NavHostController, viewModel: NavViewModel) {
    val actions = remember(navController) { NavActions(navController) }

    NavHost(navController, startDestination = Navigation.CHARACTER_LIST) {
        //首页
        composable(Navigation.CHARACTER_LIST) { CharacterList(actions.toCharacterDetail) }
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
            CharacterBasicInfo(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                r6Id = arguments.getInt(Navigation.UNIT_SIX_ID)
            )
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
}

@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {
    /**
     * 页面等级
     */
    val pageLevel = MutableLiveData(0)
}