package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.MainActivity.Companion.pageLevel
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.Navigation
import cn.wthee.pcrtool.ui.home.CharacterList
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
        var pageLevel = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PcrtoolcomposeTheme {
                Home()
            }
        }
        //设置 handler
        setHandler()
        //数据库版本检查
        GlobalScope.launch {
            val sp = getSharedPreferences("main", Context.MODE_PRIVATE)
            DatabaseUpdater.setSp(sp)
            DatabaseUpdater.checkDBVersion()
        }
    }

    //TODO 刷新页面
    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            viewModelStore.clear()
//            val fm = supportFragmentManager
//            for (i in 0..fm.backStackEntryCount) {
//                fm.popBackStack()
//            }
//            val navHostController = findNavController(R.id.nav_host_fragment)
//            for (i in 0..navHostController.backStack.size) {
//                navHostController.popBackStack()
//            }
            recreate()
            when (it.what) {
                //正常更新
                -1, 0 -> {
                    ToastUtil.short(Constants.NOTICE_TOAST_SUCCESS)
                }
                //数据切换
                1 -> {
                    ToastUtil.short(Constants.NOTICE_TOAST_CHANGE_SUCCESS)
                }
            }
            return@Callback true
        })
    }
}

@Composable
fun Home() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = Navigation.CHARACTER_LIST,
    ) {
        composable(Navigation.CHARACTER_LIST) { CharacterList(navController) }
    }
//    Box(modifier = Modifier.fillMaxSize()) {
//        Surface(modifier = Modifier.fillMaxSize()) {
//
//
//        }
//        FabMain(Modifier.align(Alignment.BottomEnd))
//    }
}

@Composable
fun FabMain(modifier: Modifier) {
    var mainIcon by remember { mutableStateOf(true) }
    FloatingActionButton(
        onClick = {
            mainIcon = pageLevel <= 0

        },
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier
            .padding(Dimen.fabMargin)
            .size(Dimen.fabSize),
    ) {
        val icon =
            painterResource(id = if (mainIcon) R.drawable.ic_function else R.drawable.ic_left)
        Icon(icon, "", modifier = Modifier.padding(Dimen.fabPadding))
    }
}

@Preview
@Composable
fun PreviewMain() {
    PcrtoolcomposeTheme() {
        Home()
    }
}
