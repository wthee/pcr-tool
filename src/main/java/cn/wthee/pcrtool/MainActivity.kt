package cn.wthee.pcrtool

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.viewbinding.ViewBinding
import androidx.work.WorkManager
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.*
import cn.wthee.pcrtool.ui.home.*
import cn.wthee.pcrtool.ui.tool.equip.EquipmentListFragment
import cn.wthee.pcrtool.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

/**
 * 主活动
 */
class MainActivity : AppCompatActivity() {

    companion object {
        var canClick = true

        @JvmField
        var currentCharaPosition: Int = 0
        var nowVersionName = "0.0.0"

        var pageLevel = 0
        var mFloatingWindowHeight = 0
        lateinit var handler: Handler

        //fab 默认隐藏
        lateinit var fabMain: FloatingActionButton
        lateinit var layoutDownload: FrameLayout
        lateinit var progressDownload: CircleProgressView
        lateinit var textDownload: MaterialTextView
    }

    private var menuItems = arrayListOf<ViewBinding>()
    private var menuItemIds = arrayListOf<Int>()
    private var menuItemDrawable = arrayListOf<Int>()
    private var menuItemTitles = arrayListOf<Int>()
    private lateinit var binding: ActivityMainBinding
    private val sharedCharacterViewModel by viewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setHandler()
        // 全屏显示
        setFullScreen()
        setContentView(binding.root)
        //取消其它任务
        WorkManager.getInstance(this).cancelAllWork()
        //初始化
        init()
        //数据库版本检查
        DatabaseUpdater.checkDBVersion()
        //监听
        setListener()
        //应用版本校验
        AppUpdateHelper.init(this, layoutInflater)
    }

    // 全屏显示
    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        mFloatingWindowHeight = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ScreenUtil.getHeight() - 48.dp
        else
            ScreenUtil.getWidth() - 48.dp
        super.onConfigurationChanged(newConfig)
    }

    //动画执行完之前，禁止直接返回
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        super.dispatchKeyEvent(event)
        binding.fab.setImageResource(R.drawable.ic_function)
        return true
    }

    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            when (it.what) {
                //获取版本失败
                0 -> {
                    MainScope().launch {
                        layoutDownload.visibility = View.VISIBLE
                        textDownload.text = "点击重试"
                        layoutDownload.setOnClickListener {
                            DatabaseUpdater.checkDBVersion()
                            layoutDownload.visibility = View.GONE
                        }
                        delay(5000L)
                        layoutDownload.visibility = View.GONE
                    }
                }
                //正常执行
                1 -> {
                    recreate()
                }
                //数据切换
                2 -> {
                    MainScope().launch {
                        delay(500L)
                        progressDownload.setProgress(100)
                        layoutDownload.setOnClickListener {
                            exitProcess(0)
                        }
                        for (i in 3 downTo 1) {
                            textDownload.text = "应用将在 ${i} 秒后关闭!"
                            delay(1000L)
                        }
                        exitProcess(0)
                    }
                }
            }

            return@Callback true
        })
    }

    private fun init() {
        layoutDownload = binding.layoutDownload
        progressDownload = binding.progress
        textDownload = binding.downloadText
        //菜单
        menuItems = arrayListOf(
            binding.toolEquip,
            binding.setting,
            binding.toolLeader,
            binding.toolNews,
            binding.toolPvp,
            binding.toolEvent,
            binding.toolCalendar,
            binding.toolGacha,
        )
        //菜单跳转
        menuItemIds = arrayListOf(
            R.id.action_characterListFragment_to_equipmentListFragment,
            R.id.action_characterListFragment_to_settingsFragment,
            R.id.action_characterListFragment_to_toolleaderFragment,
            R.id.action_characterListFragment_to_toolNewsFragment,
            R.id.action_characterListFragment_to_toolPvpFragment,
            R.id.action_characterListFragment_to_eventFragment,
            R.id.action_characterListFragment_to_calendarFragment,
            R.id.action_characterListFragment_to_toolGachaFragment,
        )
        //菜单标题
        menuItemTitles = arrayListOf(
            R.string.tool_equip,
            R.string.setting,
            R.string.tool_leader,
            R.string.tool_news,
            R.string.tool_pvp,
            R.string.tool_event,
            R.string.tool_calendar_hide,
            R.string.tool_gacha,
        )
        //菜单图标
        menuItemDrawable = arrayListOf(
            R.drawable.ic_equip,
            R.drawable.ic_settings,
            R.drawable.ic_leader,
            R.drawable.ic_news,
            R.drawable.ic_pvp,
            R.drawable.ic_event,
            R.drawable.ic_calendar,
            R.drawable.ic_gacha,
        )
        fabMain = binding.fab
        //获取版本名
        nowVersionName = packageManager.getPackageInfo(
            packageName,
            0
        ).versionName
        //绑定活动
        ActivityUtil.instance.currentActivity = this
        //悬浮穿高度
        mFloatingWindowHeight = ScreenUtil.getWidth() - 48.dp
    }


    private fun setListener() {
        //点击展开
        val motion = binding.motionLayout
        fabMain.setOnClickListener {
            if (pageLevel > 0) {
                goBack(this)
            } else {
                if (motion.currentState == R.id.start) {
                    openFab()
                } else {
                    closeFab()
                }
            }
        }
        //长按回到顶部
        fabMain.setOnLongClickListener {
            try {
                CharacterListFragment.characterList.smoothScrollToPosition(0)
                EquipmentListFragment.list.smoothScrollToPosition(0)
            } catch (e: Exception) {

            }
            return@setOnLongClickListener true
        }
        //初始化菜单
        menuItems.forEachIndexed { index, viewBinding ->
            //标题、图标
            MenuItemViewHelper(viewBinding as ViewMenuItemBinding).setItem(
                getString(menuItemTitles[index]),
                menuItemDrawable[index]
            )
            //点击事件
            viewBinding.root.setOnClickListener {
                closeMenus()
                it.transitionName = getString(menuItemTitles[index])
                val extras = FragmentNavigatorExtras(
                    it to it.transitionName
                )
                //页面跳转
                findNavController(R.id.nav_host_fragment).navigate(
                    menuItemIds[index],
                    null,
                    null,
                    extras
                )
            }
        }
    }

    // 关闭菜单
    private fun closeFab() {
        fabMain.setImageResource(R.drawable.ic_function)
        binding.motionLayout.apply {
            transitionToStart()
            isClickable = false
            isFocusable = false
        }
    }

    private fun closeMenus() {
        fabMain.setImageResource(R.drawable.ic_left)
        menuItems.forEach {
            it.root.isClickable = false
            it.root.isFocusable = false
        }
        binding.layoutBg.apply {
            isClickable = false
            isFocusable = false
        }
        binding.motionLayout.apply {
            transitionToStart()
            isClickable = false
            isFocusable = false
        }
    }

    // 打开菜单
    private fun openFab() {
        fabMain.setImageResource(R.drawable.ic_left)
        menuItems.forEach {
            it.root.isClickable = true
            it.root.isFocusable = true
        }
        binding.layoutBg.apply {
            isClickable = true
            isFocusable = true
        }
        binding.motionLayout.apply {
            transitionToEnd()
            isClickable = true
            isFocusable = true
        }
        binding.layoutBg.setOnClickListener {
            closeFab()
        }
    }

    //返回
    private fun goBack(activity: FragmentActivity) {
        if (pageLevel > 0) {
            if (pageLevel == 1) FabHelper.setIcon(R.drawable.ic_function)
            activity.findNavController(R.id.nav_host_fragment).navigateUp()
            pageLevel--
        }
    }

}
