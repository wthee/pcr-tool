package cn.wthee.pcrtool

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import androidx.work.WorkManager
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.*
import cn.wthee.pcrtool.ui.home.*
import cn.wthee.pcrtool.ui.tool.equip.EquipmentViewModel
import cn.wthee.pcrtool.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.system.exitProcess

/**
 * 主活动
 */
class MainActivity : AppCompatActivity() {

    companion object {
        var canClick = true

        @JvmField
        var currentCharaPosition: Int = 0
        var errorPicIds = arrayListOf<Int>()
        var currentEquipPosition: Int = 0
        var currentMainPage: Int = 0
        var nowVersionName = "0.0.0"
        lateinit var sp: SharedPreferences

        var canBack = true
        var pageLevel = 0
        var mFloatingWindowHeight = 0
        lateinit var handler: Handler

        //fab 默认隐藏
        lateinit var fabMain: FloatingActionButton
    }

    private var menuItems = arrayListOf<ViewBinding>()
    private lateinit var binding: ActivityMainBinding
    private val sharedCharacterViewModel by viewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private val sharedEquipViewModel by viewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
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
        //菜单布局
        initMenuItems()
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
        return if (!canBack && event.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            binding.fab.setImageResource(R.drawable.ic_function)
            super.dispatchKeyEvent(event)
        }
    }

    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            when (it.what) {
                //获取版本失败
                0 -> {
                    val layout = LayoutWarnDialogBinding.inflate(layoutInflater)
                    //弹窗
                    DialogUtil.create(
                        this,
                        layout,
                        Constants.NOTICE_TITLE_ERROR,
                        Constants.NOTICE_TOAST_TIMEOUT,
                        Constants.BTN_OPERATE_FORCE_UPDATE_DB,
                        Constants.BTN_NOT_UPDATE_DB,
                        object : DialogListener {
                            override fun onCancel(dialog: AlertDialog) {
                                //强制更新数据库
                                DatabaseUpdater.forceUpdate()
                                ToastUtil.short(Constants.NOTICE_TOAST_TITLE_DB_DOWNLOAD)
                                dialog.dismiss()
                            }

                            override fun onConfirm(dialog: AlertDialog) {
                                dialog.dismiss()
                            }
                        }
                    ).show()
                }
                //正常执行
                1 -> {
                    sharedCharacterViewModel.reload.postValue(true)
                }
                //数据切换
                2 -> {
                    val layout = LayoutWarnDialogBinding.inflate(layoutInflater)
                    //弹窗
                    DialogUtil.create(
                        this,
                        layout,
                        getString(R.string.change_success),
                        getString(R.string.change_success_tip),
                        getString(R.string.close_app),
                        getString(R.string.close_app_too),
                        object : DialogListener {
                            override fun onCancel(dialog: AlertDialog) {
                                exitProcess(0)
                            }

                            override fun onConfirm(dialog: AlertDialog) {
                                exitProcess(0)
                            }
                        }
                    ).show()
                }
            }

            return@Callback true
        })
    }

    private fun init() {
        //获取数据
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
        fabMain = binding.fab
        //获取版本名
        nowVersionName = packageManager.getPackageInfo(
            packageName,
            0
        ).versionName
        //本地储存
        sp = getSharedPreferences("main", Context.MODE_PRIVATE)
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
        //设置
        binding.setting.root.setOnClickListener {
            closeMenus()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_characterListFragment_to_settingsFragment)
        }
        //搜索
//        binding.root.setOnClickListener {
//            closeMenus()
//            //显示搜索布局
//            val layout = LayoutSearchBinding.inflate(layoutInflater)
//            val dialog = DialogUtil.create(this, layout.root)
//            dialog.show()
//            //搜索框
//            val searchView = layout.searchInput
//            searchView.onActionViewExpanded()
//            searchView.isSubmitButtonEnabled = true
//            when (currentMainPage) {
//                0 -> searchView.queryHint = "角色名"
//                1 -> searchView.queryHint = "装备名"
//            }
//            //搜索监听
//            searchView.setOnQueryTextListener(object :
//                SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    when (currentMainPage) {
//                        0 -> query?.let {
//                            sharedCharacterViewModel.getCharacters(sortType, sortAsc, query)
//                        }
//                        1 -> query?.let {
//                            sharedEquipViewModel.getEquips(query)
//                        }
//                    }
//
//                    return false
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    return false
//                }
//
//            })
//            //清空监听
//            val closeBtn =
//                searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
//            closeBtn.setOnClickListener {
//                when (currentMainPage) {
//                    0 -> sharedCharacterViewModel.getCharacters(
//                        sortType,
//                        sortAsc, ""
//                    )
//                    1 -> sharedEquipViewModel.getEquips("")
//
//                }
//                searchView.setQuery("", false)
//            }
//            //关闭搜索监听
//            dialog.setOnDismissListener {
//                try {
//                    closeFab()
//                } catch (e: Exception) {
//                }
//            }
//        }
        //筛选
//        binding.filter.root.setOnClickListener {
//            closeMenus()
//            when (currentMainPage) {
//                //装备筛选
//                1 -> {
//                    //筛选
//                    val layout = LayoutFilterEquipmentBinding.inflate(layoutInflater)
//                    //添加类型信息
//                    val chips = layout.chipsType
//                    //收藏初始
//                    layout.chipsStars.forEachIndexed { index, view ->
//                        val chip = view as Chip
//                        chip.isChecked =
//                            (EquipmentListFragment.equipFilterParams.all
//                                    && index == 0)
//                                    || (!EquipmentListFragment.equipFilterParams.all
//                                    && index == 1)
//                    }
//                    //类型
//                    EquipmentListFragment.equipTypes.forEachIndexed { _, type ->
//                        val chip = LayoutChipBinding.inflate(layoutInflater).root
//                        chip.text = type
//                        chip.isCheckable = true
//                        chip.isClickable = true
//                        chips.addView(chip)
//                        if (EquipmentListFragment.equipFilterParams.type == type) {
//                            chip.isChecked = true
//                        }
//                    }
//                    //显示弹窗
//                    val dialog = DialogUtil.create(this, layout.root, getString(R.string.reset),
//                        getString(R.string.next), object : DialogListener {
//                            override fun onCancel(dialog: AlertDialog) {
//                                sharedEquipViewModel.reset.postValue(true)
//                            }
//
//                            override fun onConfirm(dialog: AlertDialog) {
//                                //筛选选项
//                                val chip =
//                                    layout.root.findViewById<Chip>(layout.chipsType.checkedChipId)
//                                EquipmentListFragment.equipFilterParams.type = chip.text.toString()
//                                //收藏
//                                EquipmentListFragment.equipFilterParams.all =
//                                    when (layout.chipsStars.checkedChipId) {
//                                        R.id.star_0 -> true
//                                        R.id.star_1 -> false
//                                        else -> true
//                                    }
//                                sharedEquipViewModel.getEquips("")
//                            }
//                        })
//                    dialog.show()
//                    dialog.setOnDismissListener {
//                        closeFab()
//                    }
//                }
//            }
//        }
        //pvp
        binding.toolPvp.root.setOnClickListener {
            closeMenus()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_characterListFragment_to_toolPvpFragment)
        }
        //新闻
        binding.toolNews.root.setOnClickListener {
            closeMenus()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_characterListFragment_to_toolNewsFragment)
        }
        //排名
        binding.toolLeader.root.setOnClickListener {
            closeMenus()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_characterListFragment_to_toolleaderFragment)
        }
        //活动
        binding.toolEvent.root.setOnClickListener {
            closeMenus()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_characterListFragment_to_eventFragment)
        }
        //卡池
        binding.toolGacha.root.setOnClickListener {
            closeMenus()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_characterListFragment_to_toolGachaFragment)
        }
        //日历
        binding.toolCalendar.root.setOnClickListener {
            closeMenus()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_characterListFragment_to_calendarFragment)
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
        if (canBack && pageLevel > 0) {
            if (pageLevel == 1) FabHelper.setIcon(R.drawable.ic_function)
            activity.findNavController(R.id.nav_host_fragment).navigateUp()
            pageLevel--
        }
    }

    // 菜单初始
    private fun initMenuItems() {
        binding.apply {
            MenuItemViewHelper(setting).setItem(
                getString(R.string.setting),
                R.drawable.ic_settings,
                R.color.colorPrimary
            )
            MenuItemViewHelper(toolEquip).setItem(
                getString(R.string.tool_equip),
                R.drawable.ic_equip,
                R.color.colorPrimary
            )
            MenuItemViewHelper(toolPvp).setItem(
                getString(R.string.tool_pvp),
                R.drawable.ic_pvp,
                R.color.colorPrimary
            )
            MenuItemViewHelper(toolNews).setItem(
                getString(R.string.tool_news),
                R.drawable.ic_news,
                R.color.colorPrimary
            )
            MenuItemViewHelper(toolLeader).setItem(
                getString(R.string.tool_leader),
                R.drawable.ic_leader,
                R.color.colorPrimary
            )
            MenuItemViewHelper(toolEvent).setItem(
                getString(R.string.tool_event),
                R.drawable.ic_event,
                R.color.colorPrimary
            )
            MenuItemViewHelper(toolGacha).setItem(
                getString(R.string.tool_gacha),
                R.drawable.ic_gacha,
                R.color.colorPrimary
            )
            MenuItemViewHelper(toolCalendar).setItem(
                "",
                R.drawable.ic_calendar,
                R.color.colorPrimary
            ).setCenterIcon()
        }
    }
}
