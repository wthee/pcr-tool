package cn.wthee.pcrtool

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEachIndexed
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.*
import cn.wthee.pcrtool.enums.SortType
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.CharacterListFragment.Companion.guilds
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.ui.main.EquipmentListFragment
import cn.wthee.pcrtool.ui.main.EquipmentViewModel
import cn.wthee.pcrtool.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        var currentCharaPosition: Int = 0
        var currentEquipPosition: Int = 0
        var currentMainPage: Int = 0
        var nowVersionName = "0.0.0"
        lateinit var sp: SharedPreferences
        lateinit var spSetting: SharedPreferences
        var sortType = SortType.SORT_DATE
        var sortAsc = Constants.SORT_ASC
        var canBack = true
        var pageLevel = 0
        var isForeground = true
        var mHeight = 0

        //fab 默认隐藏
        lateinit var fabMain: FloatingActionButton
    }

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
        setContentView(binding.root)
        //取消其它任务
        WorkManager.getInstance(this).cancelAllWork()
        //初始化
        init()
        DatabaseUpdater.checkDBVersion()
        //悬浮按钮
        setFab()
        setListener()
        //绑定活动
        ActivityUtil.instance.currentActivity = this
        mHeight = ScreenUtil.getWidth() - 48.dp
        //应用版本校验
        AppUpdateHelper.init(this, layoutInflater)
        //菜单布局
        initMenuItems()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        mHeight = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ScreenUtil.getHeight() - 48.dp
        else
            ScreenUtil.getWidth() - 48.dp
        super.onConfigurationChanged(newConfig)
    }


    override fun onPause() {
        super.onPause()
        isForeground = false
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
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

    private fun init() {
        //获取版本名
        nowVersionName = packageManager.getPackageInfo(
            packageName,
            0
        ).versionName
        //本地储存
        sp = getSharedPreferences("main", Context.MODE_PRIVATE)
        spSetting = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun setFab() {
        fabMain = binding.fab
    }

    private fun setListener() {

        //长按回到顶部
        fabMain.setOnLongClickListener {
            when (currentMainPage) {
                0 -> CharacterListFragment.characterList.scrollToPosition(0)
                1 -> EquipmentListFragment.list.scrollToPosition(0)
            }
            return@setOnLongClickListener true
        }
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
            closeFab()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_containerFragment_to_settingsFragment)
        }
        //搜索
        binding.search.root.setOnClickListener {
            closeFab()
            //显示搜索布局
            val layout = LayoutSearchBinding.inflate(layoutInflater)
            val dialog = DialogUtil.create(this, layout.root)
            dialog.show()
            //搜索框
            val searchView = layout.searchInput
            searchView.onActionViewExpanded()
            searchView.isSubmitButtonEnabled = true
            when (currentMainPage) {
                0 -> searchView.queryHint = "角色名"
                1 -> searchView.queryHint = "装备名"
            }
            //搜索监听
            searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    when (currentMainPage) {
                        0 -> query?.let {
                            sharedCharacterViewModel.getCharacters(sortType, sortAsc, query)
                        }
                        1 -> query?.let {
                            sharedEquipViewModel.getEquips(query)
                        }
                    }

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
            //清空监听
            val closeBtn =
                searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeBtn.setOnClickListener {
                when (currentMainPage) {
                    0 -> sharedCharacterViewModel.getCharacters(
                        sortType,
                        sortAsc, ""
                    )
                    1 -> sharedEquipViewModel.getEquips("")

                }
                searchView.setQuery("", false)
            }
            //关闭搜索监听
            dialog.setOnDismissListener {
                try {
                    MainPagerFragment.tipText.visibility = View.GONE
                } catch (e: Exception) {
                }
            }
        }
        //筛选
        binding.filter.root.setOnClickListener {
            closeFab()
            when (currentMainPage) {
                //角色筛选
                0 -> {
                    //筛选
                    val layout = LayoutFilterCharacterBinding.inflate(layoutInflater)
                    val chips = layout.chipsGuild
                    //添加公会信息
                    guilds.forEachIndexed { _, guild ->
                        val chip = LayoutChipBinding.inflate(layoutInflater).root
                        chip.text = guild
                        chip.isCheckable = true
                        chip.isClickable = true
                        chips.addView(chip)
                        if (CharacterListFragment.characterFilterParams.guild == guild) {
                            chip.isChecked = true
                        }
                    }
                    //排序类型
                    when (sortType) {
                        SortType.SORT_DATE -> layout.sortChip0.isChecked = true
                        SortType.SORT_AGE -> layout.sortChip1.isChecked = true
                        SortType.SORT_HEIGHT -> layout.sortChip2.isChecked = true
                        SortType.SORT_POSITION -> layout.sortChip3.isChecked = true
                        SortType.SORT_WEIGHT -> layout.sortChip4.isChecked = true
                    }
                    //排序规则
                    if (sortAsc) layout.asc.isChecked = true else layout.desc.isChecked = true
                    //收藏初始
                    layout.chipsStars.forEachIndexed { index, view ->
                        val chip = view as Chip
                        chip.isChecked =
                            (CharacterListFragment.characterFilterParams.all
                                    && index == 0)
                                    || (!CharacterListFragment.characterFilterParams.all
                                    && index == 1)
                    }
                    //位置初始
                    layout.chipsPosition.forEachIndexed { index, view ->
                        val chip = view as Chip
                        chip.isChecked =
                            CharacterListFragment.characterFilterParams.positon == index
                    }
                    //攻击类型初始
                    layout.chipsAtk.forEachIndexed { index, view ->
                        val chip = view as Chip
                        chip.isChecked = CharacterListFragment.characterFilterParams.atk == index
                    }
                    //显示弹窗
                    DialogUtil.create(this, layout.root, getString(R.string.reset),
                        getString(R.string.next), object : DialogListener {
                            override fun onCancel(dialog: AlertDialog) {
                                sharedCharacterViewModel.reset.postValue(true)
                            }

                            override fun onConfirm(dialog: AlertDialog) {
                                //排序选项
                                sortType = when (layout.sortTypeChips.checkedChipId) {
                                    R.id.sort_chip_0 -> SortType.SORT_DATE
                                    R.id.sort_chip_1 -> SortType.SORT_AGE
                                    R.id.sort_chip_2 -> SortType.SORT_HEIGHT
                                    R.id.sort_chip_3 -> SortType.SORT_WEIGHT
                                    R.id.sort_chip_4 -> SortType.SORT_POSITION
                                    else -> SortType.SORT_DATE
                                }
                                sortAsc = layout.ascChips.checkedChipId == R.id.asc
                                //收藏
                                CharacterListFragment.characterFilterParams.all =
                                    when (layout.chipsStars.checkedChipId) {
                                        R.id.star_0 -> true
                                        R.id.star_1 -> false
                                        else -> true
                                    }
                                //位置
                                CharacterListFragment.characterFilterParams.positon =
                                    when (layout.chipsPosition.checkedChipId) {
                                        R.id.position_chip_1 -> 1
                                        R.id.position_chip_2 -> 2
                                        R.id.position_chip_3 -> 3
                                        else -> 0
                                    }
                                //攻击类型
                                CharacterListFragment.characterFilterParams.atk =
                                    when (layout.chipsAtk.checkedChipId) {
                                        R.id.atk_chip_1 -> 1
                                        R.id.atk_chip_2 -> 2
                                        else -> 0
                                    }
                                //公会筛选
                                val chip =
                                    layout.root.findViewById<Chip>(layout.chipsGuild.checkedChipId)
                                CharacterListFragment.characterFilterParams.guild =
                                    chip.text.toString()
                                //筛选
                                sharedCharacterViewModel.getCharacters(
                                    sortType,
                                    sortAsc, ""
                                )
                            }
                        }).show()
                }
                //装备筛选
                1 -> {
                    //筛选
                    val layout = LayoutFilterEquipmentBinding.inflate(layoutInflater)
                    //添加类型信息
                    val chips = layout.chipsType
                    //收藏初始
                    layout.chipsStars.forEachIndexed { index, view ->
                        val chip = view as Chip
                        chip.isChecked =
                            (EquipmentListFragment.equipFilterParams.all
                                    && index == 0)
                                    || (!EquipmentListFragment.equipFilterParams.all
                                    && index == 1)
                    }
                    //类型
                    EquipmentListFragment.equipTypes.forEachIndexed { _, type ->
                        val chip = LayoutChipBinding.inflate(layoutInflater).root
                        chip.text = type
                        chip.isCheckable = true
                        chip.isClickable = true
                        chips.addView(chip)
                        if (EquipmentListFragment.equipFilterParams.type == type) {
                            chip.isChecked = true
                        }
                    }
                    //显示弹窗
                    DialogUtil.create(this, layout.root, getString(R.string.reset),
                        getString(R.string.next), object : DialogListener {
                            override fun onCancel(dialog: AlertDialog) {
                                sharedEquipViewModel.reset.postValue(true)
                            }

                            override fun onConfirm(dialog: AlertDialog) {
                                //筛选选项
                                val chip =
                                    layout.root.findViewById<Chip>(layout.chipsType.checkedChipId)
                                EquipmentListFragment.equipFilterParams.type = chip.text.toString()
                                //收藏
                                EquipmentListFragment.equipFilterParams.all =
                                    when (layout.chipsStars.checkedChipId) {
                                        R.id.star_0 -> true
                                        R.id.star_1 -> false
                                        else -> true
                                    }
                                sharedEquipViewModel.getEquips("")
                            }
                        }).show()
                }
            }
        }
        //pvp
        binding.toolPvp.root.setOnClickListener {
            closeFab()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_containerFragment_to_toolPvpFragment)
        }
        //新闻
        binding.toolNews.root.setOnClickListener {
            closeFab()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_containerFragment_to_toolNewsFragment)
        }
        //排名
        binding.toolLeader.root.setOnClickListener {
            closeFab()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_containerFragment_to_toolLeaderFragment)
        }
        //活动
        binding.toolEvent.root.setOnClickListener {
            closeFab()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_containerFragment_to_eventFragment)
        }
        //卡池
        binding.toolGacha.root.setOnClickListener {
            closeFab()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_containerFragment_to_toolGachaFragment)
        }
    }

    private fun closeFab() {
        binding.motionLayout.apply {
            MainScope().launch {
                transitionToStart()
                setBackgroundColor(getColor(R.color.colorAlpha))
                isClickable = false
                isFocusable = false
            }
        }
        fabMain.setImageResource(R.drawable.ic_function)
    }

    private fun openFab() {
        fabMain.setImageResource(R.drawable.ic_cancel)
        binding.motionLayout.apply {
            transitionToEnd()
            setBackgroundColor(getColor(R.color.colorAlphtBlack))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                closeFab()
            }
        }
    }

    private fun goBack(activity: FragmentActivity) {
        if (canBack && pageLevel > 0) {
            if (pageLevel == 1) FabHelper.setIcon(R.drawable.ic_function)
            activity.findNavController(R.id.nav_host_fragment).navigateUp()
            pageLevel--
        }
    }

    private fun initMenuItems() {
        binding.apply {
            MenuItemViewHelper(setting).setItem(
                getString(R.string.setting),
                R.drawable.ic_settings,
                R.color.colorPrimary
            )
            MenuItemViewHelper(search).setItem(
                getString(R.string.search),
                R.drawable.ic_search,
                R.color.colorPrimary
            )
            MenuItemViewHelper(filter).setItem(
                getString(R.string.filter),
                R.drawable.ic_filter,
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
        }
    }
}
