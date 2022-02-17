package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.model.getIds
import cn.wthee.pcrtool.ui.common.GridIconListCompose
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.common.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.GachaViewModel

/**
 *
 */
@Composable
fun MockGacha() {

    Box(modifier = Modifier.fillMaxSize()) {
        UnitList()
    }
}

@Composable
private fun UnitList(gachaViewModel: GachaViewModel = hiltViewModel()) {
    val unitList = gachaViewModel.getGachaUnits().collectAsState(initial = null).value

    unitList?.let {
        Column(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .verticalScroll(rememberScrollState())
        ) {
            val normal3 = it.normal3.getIds()
            Row(verticalAlignment = Alignment.CenterVertically) {
                MainTitleText(text = "常驻★3")
                MainText(
                    text = normal3.size.toString(),
                    modifier = Modifier.padding(start = Dimen.mediumPadding)
                )
            }
            GridIconListCompose(icons = normal3, toCharacterDetail = {})

            val limit = it.limit.getIds()
            Row(verticalAlignment = Alignment.CenterVertically) {
                MainTitleText(text = "限定")
                MainText(
                    text = limit.size.toString(),
                    modifier = Modifier.padding(start = Dimen.mediumPadding)
                )
            }
            GridIconListCompose(icons = limit, toCharacterDetail = {})

            val fesLimit = it.fesLimit.getIds()
            Row(verticalAlignment = Alignment.CenterVertically) {
                MainTitleText(text = "FES 限定")
                MainText(
                    text = fesLimit.size.toString(),
                    modifier = Modifier.padding(start = Dimen.mediumPadding)
                )
            }
            GridIconListCompose(icons = fesLimit, toCharacterDetail = {})
        }
    }
}