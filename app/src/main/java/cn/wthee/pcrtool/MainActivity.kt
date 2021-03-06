package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.*
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.startup.AppInitializer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import androidx.work.WorkManager
import cn.wthee.circleprogressbar.CircleProgressView
import cn.wthee.pcrtool.adapter.viewpager.CharacterPagerAdapter
import cn.wthee.pcrtool.adapter.viewpager.NewsListPagerAdapter
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.*
import cn.wthee.pcrtool.ui.character.CharacterPagerFragment
import cn.wthee.pcrtool.ui.home.*
import cn.wthee.pcrtool.ui.setting.MainSettingsFragment
import cn.wthee.pcrtool.ui.tool.news.NewsPagerFragment
import cn.wthee.pcrtool.utils.*
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.umeng.commonsdk.UMConfigure
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 主页
 *
 * 页面布局 [ActivityMainBinding]
 *
 * ViewModels []
 */
class MainActivity : AppCompatActivity() {

    companion object {
        var canClick = true

        @JvmField
        var currentCharaPosition: Int = 0

        var pageLevel = 0
        var mFloatingWindowHeight = 0
        lateinit var handler: Handler

        //fab 默认隐藏
        lateinit var fabMain: FloatingActionButton
        lateinit var layoutDownload: FrameLayout
        lateinit var progressDownload: CircleProgressView
        lateinit var textDownload: MaterialTextView

        //消息通知
        lateinit var fabNotice: ExtendedFloatingActionButton

    }

    private var menuItems = arrayListOf<ViewBinding>()
    private var menuItemIds = arrayListOf<Int>()
    private var menuItemDrawable = arrayListOf<Int>()
    private var menuItemTitles = arrayListOf<Int>()
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //获取 Uri
        fixUriBug()
        //全屏显示
        setFullScreen()
        setContentView(binding.root)
        //初始化
        init()
        //友盟初始化
        AppInitializer.getInstance(applicationContext)
            .initializeComponent(UMengInitializer::class.java)
        UMConfigure.setProcessEvent(true)
        //取消其它任务
        WorkManager.getInstance(this).cancelAllWork()
        //监听
        setListener()
        //初始化 handler
        setHandler()
        GlobalScope.launch {
            //应用版本校验
            AppUpdateUtil.init()
        }
        GlobalScope.launch {
            //数据库版本检查
            DatabaseUpdater.checkDBVersion()
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

    private fun fixUriBug() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
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

    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            viewModelStore.clear()
            val fm = supportFragmentManager
            for (i in 0..fm.backStackEntryCount) {
                fm.popBackStack()
            }
            val navHostController = findNavController(R.id.nav_host_fragment)
            for (i in 0..navHostController.backStack.size) {
                navHostController.popBackStack()
            }
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

    private fun init() {
        ActivityHelper.instance.currentActivity = this
        layoutDownload = binding.layoutDownload
        progressDownload = binding.progress
        textDownload = binding.downloadText
        fabNotice = binding.fabNotice
        fabNotice.hide()
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
            binding.toolGuild,
        )
        //菜单跳转
        menuItemIds = arrayListOf(
            R.id.action_characterListFragment_to_equipmentListFragment,
            R.id.action_characterListFragment_to_settingsFragment,
            R.id.action_characterListFragment_to_toolleaderFragment,
            R.id.action_characterListFragment_to_toolNewsFragment,
            R.id.action_characterListFragment_to_toolPvpFragment,
            R.id.action_characterListFragment_to_eventFragment,
            if (DatabaseUpdater.getDatabaseType() == 1)
                R.id.action_characterListFragment_to_calendarFragment
            else
                R.id.action_characterListFragment_to_calendarJPFragment,
            R.id.action_characterListFragment_to_toolGachaFragment,
            R.id.action_characterListFragment_to_guildFragment,
        )
        //菜单标题
        menuItemTitles = arrayListOf(
            R.string.tool_equip,
            R.string.setting,
            R.string.tool_leader,
            R.string.tool_news,
            R.string.tool_pvp,
            R.string.tool_event,
            R.string.tool_calendar_title,
            R.string.tool_gacha,
            R.string.title_guild,
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
            R.drawable.ic_guild,
        )
        fabMain = binding.fab
        //悬浮穿高度
        mFloatingWindowHeight = ScreenUtil.getWidth() - 48.dp
    }


    private fun setListener() {
        //点击展开
        fabMain.setOnClickListener {
            if (pageLevel > 0) {
                goBack(this)
            } else {
                if (binding.layoutMotion.currentState == R.id.start) {
                    openFab()
                } else {
                    closeFab()
                }
            }
        }
        //长按回到顶部
        fabMain.setOnLongClickListener {
            try {
                val fragment =
                    supportFragmentManager.fragments[0].childFragmentManager.fragments[0]
                var view = fragment.view
                when (fragment) {
                    // 公告页面
                    is NewsPagerFragment -> {
                        val itemView =
                            (NewsPagerFragment.viewPager.adapter as NewsListPagerAdapter)
                                .mFragments[NewsPagerFragment.currentPage]
                                .view as LinearLayout
                        itemView.children.iterator().forEach {
                            if (it is SwipeRefreshLayout) {
                                (it.getChildAt(0) as RecyclerView).scrollToPosition(0)
                            }
                        }
                    }
                    //角色详情页面
                    is CharacterPagerFragment -> {
                        val itemView =
                            (CharacterPagerFragment.viewPager.adapter as CharacterPagerAdapter)
                                .mFragments[CharacterPagerFragment.currentPage]
                                .view as ViewGroup
                        itemView.children.iterator().forEach {
                            if (it is RecyclerView) {
                                it.scrollToPosition(0)
                            }
                        }
                        view = itemView
                    }
                    //设置页面
                    is MainSettingsFragment -> {
                        fragment.scrollToPreference(fragment.findPreference("title_database")!!)
                    }
                    else -> {
                        view?.findViewById<RecyclerView>(R.id.tool_list)
                            ?.scrollToPosition(0)
                    }
                }
                //布局复位
                view?.findViewById<MotionLayout>(R.id.layout_motion)?.transitionToStart()

            } catch (e: Exception) {
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_BACK_TOP)
                }
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
                try {
                    closeMenus()
                    //页面跳转
                    findNavController(R.id.nav_host_fragment).navigate(
                        menuItemIds[index],
                        null,
                        null,
                        null
                    )
                } catch (e: Exception) {
                    MainScope().launch {
                        UMCrash.generateCustomLog(e, Constants.EXCEPTION_MENU_NAV)
                    }
                }
            }
        }
        //打开通知
        binding.fabNotice.apply {
            setOnClickListener {
                this@MainActivity.findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_global_noticeListFragment)
            }
        }
    }

    // 关闭菜单
    private fun closeFab() {
        fabMain.setImageResource(R.drawable.ic_function)
        binding.layoutMotion.apply {
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
        binding.layoutMotion.apply {
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
        binding.layoutMotion.apply {
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
