package cn.wthee.pcrtool

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.databinding.ActivityMainBinding
import cn.wthee.pcrtool.databinding.LayoutFilterCharacterBinding
import cn.wthee.pcrtool.databinding.LayoutFilterEquipmentBinding
import cn.wthee.pcrtool.databinding.LayoutSearchBinding
import cn.wthee.pcrtool.ui.main.*
import cn.wthee.pcrtool.ui.main.EquipmentListFragment.Companion.asc
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_TODO
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        var currentCharaPosition: Int = 0
        var currentEquipPosition: Int = 0
        var currentMainPage: Int = 0
        var databaseVersion: String? = Constants.DATABASE_VERSION
        var nowVersionName = "0.0.0"
        lateinit var sp: SharedPreferences
        lateinit var spFirstClick: SharedPreferences
        lateinit var spSetting: SharedPreferences
        var sortType = Constants.SORT_TYPE
        var sortAsc = Constants.SORT_ASC
        var canBack = true
        var isHome = true
        var notToast = false
        var autoUpdateDb = true

        //fab 默认隐藏
        lateinit var fab: FloatingActionButton
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
            fab.setImageResource(R.drawable.ic_function)
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
        spFirstClick = getSharedPreferences("firstClick", Context.MODE_PRIVATE)
        databaseVersion = sp.getString(Constants.SP_DATABASE_VERSION, Constants.DATABASE_VERSION)

        //设置信息
        spSetting = PreferenceManager.getDefaultSharedPreferences(this)
        notToast = spSetting.getBoolean("not_toast", notToast)
    }

    private fun setFab() {
        fab = binding.fab
        fabSetting = binding.setting
        fabLove = binding.love
        fabSearch = binding.search
        fabFilter = binding.filter
    }

    private fun setListener() {
        //长按回到顶部
        fab.setOnLongClickListener {
            when (currentMainPage) {
                0 -> CharacterListFragment.characterList.scrollToPosition(0)
                1 -> EquipmentListFragment.list.scrollToPosition(0)
                2 -> EnemyListFragment.list.scrollToPosition(0)
            }
            return@setOnLongClickListener true
        }
        //点击展开
        val motion = binding.motionLayout
        fab.setOnClickListener {
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
                            false
                        } else {
                            ToastUtil.short("显示全部")
                            true
                        }
                    sharedCharacterViewModel.getCharacters(
                        sortType,
                        sortAsc, ""
                    )
                    CharacterListFragment.listAdapter.notifyDataSetChanged()
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
            searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    when (currentMainPage) {
                        0 -> {
                            //角色名字
                            sharedCharacterViewModel.getCharacters(
                                sortType,
                                sortAsc, query ?: ""
                            )
                        }
                        1 -> {
                            sharedEquipViewModel.getEquips(asc, query ?: "")
                        }
                        2 -> EnemyListFragment.listAdapter.filter.filter(
                            query
                        )
                    }

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //重置
                    if (newText == "") {
                        when (currentMainPage) {
                            0 -> sharedCharacterViewModel.getCharacters(
                                sortType,
                                sortAsc, ""
                            )
                            1 -> sharedEquipViewModel.getEquips(asc, "")
                            2 -> EnemyListFragment.viewModel.getAllEnemy()
                        }
                    }

                    return false
                }
            })
        }
        //筛选
        fabFilter.setOnClickListener {
            closeFab()
            when (currentMainPage) {
                0 -> {
                    //筛选
                    val layout = LayoutFilterCharacterBinding.inflate(layoutInflater)
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()
                    //排序类型
                    when (sortType) {
                        0 -> layout.sortChip0.isChecked = true
                        1 -> layout.sortChip1.isChecked = true
                        2 -> layout.sortChip2.isChecked = true
                        3 -> layout.sortChip3.isChecked = true
                    }
                    //排序规则
                    if (sortAsc) layout.asc.isChecked = true else layout.desc.isChecked = true
                    //位置筛选
                    val positionChip1 = layout.positionChip1
                    val positionChip2 = layout.positionChip2
                    val positionChip3 = layout.positionChip3
                    //传入筛选条件
                    positionChip1.isChecked = CharacterListFragment.characterfilterParams.positon1
                    positionChip2.isChecked = CharacterListFragment.characterfilterParams.positon2
                    positionChip3.isChecked = CharacterListFragment.characterfilterParams.positon3
                    //攻击类型筛选
                    val atk1 = layout.atkChip1
                    val atk2 = layout.atkChip2
                    //传入筛选条件
                    atk1.isChecked = CharacterListFragment.characterfilterParams.atkPhysical
                    atk2.isChecked = CharacterListFragment.characterfilterParams.atkMagic
                    layout.next.setOnClickListener {
                        dialog.dismiss()
                        //排序选项
                        sortType = when (layout.sortTypeChips.checkedChipId) {
                            R.id.sort_chip_0 -> 0
                            R.id.sort_chip_1 -> 1
                            R.id.sort_chip_2 -> 2
                            R.id.sort_chip_3 -> 3
                            else -> 0
                        }
                        sortAsc = layout.ascChips.checkedChipId == R.id.asc
                        //筛选选项
                        CharacterListFragment.characterfilterParams.positon1 =
                            positionChip1.isChecked
                        CharacterListFragment.characterfilterParams.positon2 =
                            positionChip2.isChecked
                        CharacterListFragment.characterfilterParams.positon3 =
                            positionChip3.isChecked
                        CharacterListFragment.characterfilterParams.atkPhysical = atk1.isChecked
                        CharacterListFragment.characterfilterParams.atkMagic = atk2.isChecked
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
                1 -> {
                    //筛选
                    val layout = LayoutFilterEquipmentBinding.inflate(layoutInflater)
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()
                    //类型筛选
                    val craft0 = layout.craftChip0
                    val craft1 = layout.craftChip1
                    //传入筛选条件
                    craft0.isChecked = EquipmentListFragment.equipfilterParams.craft0
                    craft1.isChecked = EquipmentListFragment.equipfilterParams.craft1
                    layout.next.setOnClickListener {
                        dialog.dismiss()
                        //筛选选项
                        EquipmentListFragment.equipfilterParams.craft0 = craft0.isChecked
                        EquipmentListFragment.equipfilterParams.craft1 = craft1.isChecked
                        sharedEquipViewModel.getEquips(asc, "")
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
            transitionToStart()
            setBackgroundColor(getColor(R.color.colorAlpha))
            isClickable = false
            isFocusable = false
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
