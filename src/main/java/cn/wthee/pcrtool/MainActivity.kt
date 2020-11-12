package cn.wthee.pcrtool

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEachIndexed
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
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
import kotlinx.coroutines.delay
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
        var isHome = true
        var isForeground = true
        var mHeight = 0

        //fab 默认隐藏
        lateinit var fabMain: FloatingActionButton
        lateinit var fabSetting: FloatingActionButton
        lateinit var fabLove: FloatingActionButton
        lateinit var fabSearch: FloatingActionButton
        lateinit var fabFilter: FloatingActionButton
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
        DatabaseUpdateHelper.checkDBVersion()
        //悬浮按钮
        setFab()
        setListener()
        //绑定活动
        ActivityUtil.instance.currentActivity = this
        mHeight = ScreenUtil.getWidth(this) - 48.dp

        //状态栏适配 TODO 替换 Deprecated 的方法
        val layoutParams = WindowManager.LayoutParams()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
//        var systemUiVisibility: Int = window.decorView.getSystemUiVisibility()
//        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.decorView.systemUiVisibility = systemUiVisibility

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            mHeight = ScreenUtil.getHeight(this) - 48.dp
        else
            mHeight = ScreenUtil.getWidth(this) - 48.dp
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
        fabSetting = binding.setting
        fabLove = binding.love
        fabSearch = binding.search
        fabFilter = binding.filter
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
            if (!isHome) {
                FabHelper.goBack(this)
            } else {
                if (motion.currentState == R.id.start) {
                    openFab()
                } else {
                    closeFab()
                }
            }
        }
        //设置
        fabSetting.setOnClickListener {
            closeFab()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_containerFragment_to_settingsFragment)
        }
        //收藏
        fabLove.setOnClickListener {
            closeFab()
            when (currentMainPage) {
                0 -> {
                    CharacterListFragment.characterfilterParams.all =
                        if (CharacterListFragment.characterfilterParams.all) {
                            ToastUtil.short("仅显示收藏")
                            MainScope().launch {
                                delay(300L)
                                fabLove.setImageResource(R.drawable.ic_loved)
                            }
                            false
                        } else {
                            ToastUtil.short("显示全部")
                            MainScope().launch {
                                delay(300L)
                                fabLove.setImageResource(R.drawable.ic_love_hollow)
                            }
                            true
                        }
                    sharedCharacterViewModel.getCharacters(
                        sortType,
                        sortAsc, ""
                    )
                }
                1 -> {
                    EquipmentListFragment.equipFilterParams.all =
                        if (EquipmentListFragment.equipFilterParams.all) {
                            ToastUtil.short("仅显示收藏")
                            MainScope().launch {
                                delay(300L)
                                fabLove.setImageResource(R.drawable.ic_loved)
                            }
                            false
                        } else {
                            ToastUtil.short("显示全部")
                            MainScope().launch {
                                delay(300L)
                                fabLove.setImageResource(R.drawable.ic_love_hollow)
                            }
                            true
                        }
                    sharedEquipViewModel.getEquips("")
                }
            }
        }
        //搜索
        fabSearch.setOnClickListener {
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
        fabFilter.setOnClickListener {
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
                        if (CharacterListFragment.characterfilterParams.guild == guild) {
                            chip.isChecked = true
                        }
                    }
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()

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
                    //位置初始
                    layout.chipsPosition.forEachIndexed { index, view ->
                        val chip = view as Chip
                        chip.isChecked =
                            CharacterListFragment.characterfilterParams.positon == index
                    }
                    //攻击类型初始
                    layout.chipsAtk.forEachIndexed { index, view ->
                        val chip = view as Chip
                        chip.isChecked = CharacterListFragment.characterfilterParams.atk == index
                    }
                    layout.btns.next.setOnClickListener {
                        dialog.dismiss()
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
                        //位置
                        CharacterListFragment.characterfilterParams.positon =
                            when (layout.chipsPosition.checkedChipId) {
                                R.id.position_chip_1 -> 1
                                R.id.position_chip_2 -> 2
                                R.id.position_chip_3 -> 3
                                else -> 0
                            }
                        //攻击类型
                        CharacterListFragment.characterfilterParams.atk =
                            when (layout.chipsAtk.checkedChipId) {
                                R.id.atk_chip_1 -> 1
                                R.id.atk_chip_2 -> 2
                                else -> 0
                            }
                        //公会筛选
                        val chip = layout.root.findViewById<Chip>(layout.chipsGuild.checkedChipId)
                        CharacterListFragment.characterfilterParams.guild = chip.text.toString()
                        //筛选
                        sharedCharacterViewModel.getCharacters(
                            sortType,
                            sortAsc, ""
                        )
                    }
                    layout.btns.reset.setOnClickListener {
                        dialog.dismiss()
                        CharacterListFragment.characterfilterParams.initData()
                        CharacterListFragment.characterfilterParams.all = true
                        sortType = SortType.SORT_DATE
                        sortAsc = false
                        sharedCharacterViewModel.getCharacters(
                            sortType,
                            sortAsc, ""
                        )
                    }
                }
                //装备筛选
                1 -> {
                    //筛选
                    val layout = LayoutFilterEquipmentBinding.inflate(layoutInflater)
                    //添加类型信息
                    val chips = layout.chipsType
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
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()
                    layout.btns.next.setOnClickListener {
                        dialog.dismiss()
                        //筛选选项
                        val chip = layout.root.findViewById<Chip>(layout.chipsType.checkedChipId)
                        EquipmentListFragment.equipFilterParams.type = chip.text.toString()
                        sharedEquipViewModel.getEquips("")

                    }
                    layout.btns.reset.setOnClickListener {
                        dialog.dismiss()
                        EquipmentListFragment.equipFilterParams.initData()
                        sharedEquipViewModel.getEquips("")
                    }
                }
            }
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
        binding.motionLayout.apply {
            transitionToEnd()
            setBackgroundColor(getColor(R.color.colorAlphtBlack))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                closeFab()
            }
        }
        fabMain.setImageResource(R.drawable.ic_back)
    }
}
