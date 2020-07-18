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
import cn.wthee.pcrtool.databinding.LayoutFilterBinding
import cn.wthee.pcrtool.databinding.LayoutSearchBinding
import cn.wthee.pcrtool.databinding.LayoutSortBinding
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.ui.main.EnemyListFragment
import cn.wthee.pcrtool.ui.main.EquipmentListFragment
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
        lateinit var spSetting: SharedPreferences
        var sortType = Constants.SORT_TYPE
        var sortAsc = Constants.SORT_ASC
        var canBack = true
        var isHome = true

        //fab 默认隐藏
        lateinit var fab: FloatingActionButton
        lateinit var fabSetting: FloatingActionButton
        lateinit var fabLove: FloatingActionButton
        lateinit var fabSearch: FloatingActionButton
        lateinit var fabFilter: FloatingActionButton
        lateinit var fabSort: FloatingActionButton
    }

    private lateinit var binding: ActivityMainBinding
    private val sharedCharacterViewModel by viewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
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
        val autoUpdateDb = spSetting.getBoolean("auto_update_db", true)
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
        if (autoUpdateDb) {
            CoroutineScope(Dispatchers.Main).launch {
                DatabaseUpdateHelper().checkDBVersion()
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
        databaseVersion = sp.getString(Constants.SP_DATABASE_VERSION, Constants.DATABASE_VERSION)
        sortType = sp.getInt(Constants.SP_SORT_TYPE, Constants.SORT_TYPE)
        sortAsc = sp.getBoolean(Constants.SP_SORT_ASC, Constants.SORT_ASC)
        //设置信息
        spSetting = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun setFab() {
        fab = binding.fab
        fabSetting = binding.setting
        fabLove = binding.love
        fabSearch = binding.search
        fabFilter = binding.filter
        fabSort = binding.sort
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
                    CharacterListFragment.filterParams.all =
                        if (CharacterListFragment.filterParams.all) {
                            ToastUtil.short("仅显示收藏")
                            false
                        } else {
                            ToastUtil.short("显示全部")
                            true
                        }
                    sharedCharacterViewModel.getCharacters(
                        sortType,
                        sortAsc, "", mapOf()
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
                                sortAsc, query ?: "", mapOf()
                            )
                        }
                        1 -> EquipmentListFragment.listAdapter.filter.filter(
                            query
                        )
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
                                sortAsc, "", mapOf()
                            )
                            1 -> EquipmentListFragment.viewModel.getEquips()
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
                    val layout = LayoutFilterBinding.inflate(layoutInflater)
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()
                    //位置筛选
                    val positionChip1 = layout.positionChip1
                    val positionChip2 = layout.positionChip2
                    val positionChip3 = layout.positionChip3
                    //传入筛选条件
                    positionChip1.isChecked = CharacterListFragment.filterParams.positon1
                    positionChip2.isChecked = CharacterListFragment.filterParams.positon2
                    positionChip3.isChecked = CharacterListFragment.filterParams.positon3
                    //攻击类型筛选
                    val atk1 = layout.atkChip1
                    val atk2 = layout.atkChip2
                    //传入筛选条件
                    atk1.isChecked = CharacterListFragment.filterParams.atkPhysical
                    atk2.isChecked = CharacterListFragment.filterParams.atkMagic
                    layout.next.setOnClickListener {
                        dialog.dismiss()
                        //筛选选项
                        CharacterListFragment.filterParams.positon1 = positionChip1.isChecked
                        CharacterListFragment.filterParams.positon2 = positionChip2.isChecked
                        CharacterListFragment.filterParams.positon3 = positionChip3.isChecked
                        CharacterListFragment.filterParams.atkPhysical = atk1.isChecked
                        CharacterListFragment.filterParams.atkMagic = atk2.isChecked
                        sharedCharacterViewModel.getCharacters(
                            sortType,
                            sortAsc, "", mapOf()
                        )
                    }
                }
                1 -> {
                    ToastUtil.short(NOTICE_TOAST_TODO)
                }
                2 -> {
                    ToastUtil.short(NOTICE_TOAST_TODO)
                }
            }
        }
        //排序
        fabSort.setOnClickListener {
            closeFab()
            when (currentMainPage) {
                0 -> {
                    //显示排序布局
                    val layout = LayoutSortBinding.inflate(layoutInflater)
                    val dialog = DialogUtil.create(this, layout.root)
                    dialog.show()
                    layout.next.setOnClickListener {
                        dialog.dismiss()
                        //筛选选项
                        sortType = layout.spinnerSort.selectedItemPosition
                        sortAsc = layout.radioSort.checkedRadioButtonId == R.id.sort_asc
                        sharedCharacterViewModel.getCharacters(
                            sortType,
                            sortAsc, "", mapOf()
                        )
                    }
                }
                1 -> {
                    ToastUtil.short(NOTICE_TOAST_TODO)
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
