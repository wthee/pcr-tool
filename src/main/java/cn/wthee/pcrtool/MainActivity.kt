package cn.wthee.pcrtool

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
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
import cn.wthee.pcrtool.ui.main.*
import cn.wthee.pcrtool.ui.main.CharacterListFragment.Companion.guilds
import cn.wthee.pcrtool.ui.main.EquipmentListFragment.Companion.asc
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_TODO
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        var currentCharaPosition: Int = 0
        var currentEquipPosition: Int = 0
        var currentMainPage: Int = 0
        var nowVersionName = "0.0.0"
        lateinit var sp: SharedPreferences
        lateinit var spSetting: SharedPreferences
        var sortType = Constants.SORT_TYPE
        var sortAsc = Constants.SORT_ASC
        var canBack = true
        var isHome = true
        var notToast = false
        var autoUpdateDb = true

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
        //检查数据库更新
        autoUpdateDb = spSetting.getBoolean("auto_update_db", autoUpdateDb)
        checkUpdate(autoUpdateDb)
        //悬浮按钮
        setFab()
        setListener()
        //绑定活动
        ActivityUtil.instance.currentActivity = this
        // Bugly 初始设置
        BuglyHelper.init(this)

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

    private fun checkUpdate(autoUpdateDb: Boolean) {
        if (FileUtil.needUpadateDb() || autoUpdateDb) {
            CoroutineScope(Dispatchers.Main).launch {
                DatabaseUpdateHelper().checkDBVersion(notToast)
            }
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
        //设置信息
        spSetting = PreferenceManager.getDefaultSharedPreferences(this)
        notToast = spSetting.getBoolean("not_toast", notToast)
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
                2 -> EnemyListFragment.list.scrollToPosition(0)
            }
            return@setOnLongClickListener true
        }
        //点击展开
        val motion = binding.motionLayout
        fabMain.setOnClickListener {
            if (!isHome) {
                FabHelper.goBack()
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
                    ToastUtil.short(NOTICE_TOAST_TODO)
                }
                2 -> {
                    ToastUtil.short(NOTICE_TOAST_TODO)
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
                2 -> searchView.queryHint = "怪物名"
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
                            sharedEquipViewModel.getEquips(asc, query ?: "")
                        }
                        2 -> query?.let {
                            sharedCharacterViewModel.getCharacters(sortType, sortAsc, query)
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
                    1 -> sharedEquipViewModel.getEquips(asc, "")
                    2 -> EnemyListFragment.viewModel.getAllEnemy()
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
                    MainScope().launch {
                        guilds.forEachIndexed { index, guild ->
                            val chip = LayoutChipBinding.inflate(layoutInflater).root
                            chip.text = guild
                            chip.isCheckable = true
                            chip.isClickable = true
                            chips.addView(chip)
                            if (CharacterListFragment.characterfilterParams.guild == guild) {
                                chip.isChecked = true
                            }
                        }
                    }
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()
                    //排序类型
                    when (sortType) {
                        0 -> layout.sortChip0.isChecked = true
                        1 -> layout.sortChip1.isChecked = true
                        2 -> layout.sortChip2.isChecked = true
                        3 -> layout.sortChip3.isChecked = true
                        4 -> layout.sortChip4.isChecked = true
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
                    layout.next.setOnClickListener {
                        dialog.dismiss()
                        //排序选项
                        sortType = when (layout.sortTypeChips.checkedChipId) {
                            R.id.sort_chip_0 -> 0
                            R.id.sort_chip_1 -> 1
                            R.id.sort_chip_2 -> 2
                            R.id.sort_chip_3 -> 3
                            R.id.sort_chip_4 -> 4
                            else -> 0
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
                    layout.reset.setOnClickListener {
                        dialog.dismiss()
                        MainPagerFragment.tabLayout.selectTab(MainPagerFragment.tabLayout.getTabAt(0))
                    }
                }
                //装备筛选
                1 -> {
                    //筛选
                    val layout = LayoutFilterEquipmentBinding.inflate(layoutInflater)
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()
                    //类型筛选
                    layout.craftChips.forEachIndexed { index, view ->
                        val chip = view as Chip
                        chip.isChecked = EquipmentListFragment.equipfilterParams.craft == index
                    }
                    layout.next.setOnClickListener {
                        dialog.dismiss()
                        //筛选选项
                        EquipmentListFragment.equipfilterParams.craft =
                            when (layout.craftChips.checkedChipId) {
                                R.id.craft_chip_1 -> 1
                                R.id.craft_chip_2 -> 2
                                else -> 0
                            }
                        sharedEquipViewModel.getEquips(asc, "")
                    }
                    layout.reset.setOnClickListener {
                        dialog.dismiss()
                        MainPagerFragment.tabLayout.selectTab(MainPagerFragment.tabLayout.getTabAt(1))
                    }
                }
                2 -> {
                    ToastUtil.short(NOTICE_TOAST_TODO)
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
    }
}
