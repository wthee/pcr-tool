package cn.wthee.pcrtool.utils

import androidx.compose.runtime.*

/**
 * @author Yricky
 * @date 2022/3/20
 */

@Composable
fun <T> rememberCached(updater:()->T):T{
    var value by remember { mutableStateOf(updater()) }
    if(value != updater()){
        value = updater()
    }
    return value
}