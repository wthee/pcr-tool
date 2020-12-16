package cn.wthee.pcrtool.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.viewpager.MainPagerAdapter
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.FragmentMainPagerBinding
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.ui.detail.character.basic.CharacterBasicInfoFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.set
import kotlin.system.exitProcess

/**
 * 主页面 ViewPager
 */
class MainPagerFragment : Fragment() {

    companion object {
        var cListClick = false
        lateinit var tabLayout: TabLayout
        lateinit var tipText: MaterialTextView
        lateinit var handler: Handler
    }

    private lateinit var binding: FragmentMainPagerBinding
    private lateinit var viewPager2: ViewPager2
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private val sharedEquipViewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPagerBinding.inflate(inflater, container, false)
        init()
        prepareTransitions()
        sethandler()
        return binding.root
    }

    private fun sethandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            when (it.what) {
                //获取版本失败
                0 -> {
                    val layout = LayoutWarnDialogBinding.inflate(layoutInflater)
                    //弹窗
                    DialogUtil.create(
                        requireContext(),
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
                        requireContext(),
                        layout,
                        getString(R.string.change_success),
                        getString(R.string.change_success_tip),
                        getString(R.string.close_app),
                        getString(R.string.close_app_too),
                        object : DialogListener {
                            override fun onCancel(dialog: AlertDialog) {
                                requireActivity().finish()
                                exitProcess(0)
                            }

                            override fun onConfirm(dialog: AlertDialog) {
                                requireActivity().finish()
                                exitProcess(0)
                            }
                        }
                    ).show()
                }
            }

            return@Callback true
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            MainActivity.pageLevel = 0
            //刷新收藏
            val vh = CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                MainActivity.currentCharaPosition
            )?.itemView?.findViewById<MaterialTextView>(R.id.name)
            val color = if (CharacterBasicInfoFragment.isLoved)
                ResourcesUtil.getColor(R.color.colorPrimary)
            else
                ResourcesUtil.getColor(R.color.text)
            vh?.setTextColor(color)
            CharacterListFragment.characterList.scrollToPosition(MainActivity.currentCharaPosition)

        } catch (e: java.lang.Exception) {
        }
    }

    private fun init() {
        tipText = binding.noDataTip
        //禁止连续点击
        cListClick = false
        //viewpager2 配置
        viewPager2 = binding.mainViewPager
        viewPager2.offscreenPageLimit = 2
        viewPager2.adapter = MainPagerAdapter(requireActivity())
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MainActivity.currentMainPage = position
                when (position) {
                    0 -> tipText.text = getString(R.string.data_null_character)
                    1 -> tipText.text = getString(R.string.data_null_equip)
                }
            }
        })
        //toolbar
        ToolbarUtil(binding.mainToolbar)
            .setMainToolbar(getString(R.string.app_name))
        //tab 初始化
        tabLayout = binding.layoutTab
        //绑定tablayout
        TabLayoutMediator(
            tabLayout,
            viewPager2
        ) { tab, position ->
            when (position) {
                //角色
                0 -> {
                    tab.icon =
                        ResourcesUtil.getDrawable(R.drawable.ic_character)
                    tab.text = sp.getInt(Constants.SP_COUNT_CHARACTER, 0).toString()
                    //长按重置
                    tab.view.setOnLongClickListener {
                        sharedCharacterViewModel.reset.postValue(true)
                        return@setOnLongClickListener true
                    }
                    //点击回顶部
                    tab.view.setOnClickListener {
                        if (MainActivity.currentMainPage == position) {
                            CharacterListFragment.characterList.smoothScrollToPosition(0)
                        }
                    }
                }
                //装备
                1 -> {
                    tab.icon = ResourcesUtil.getDrawable(R.drawable.ic_equip)
                    tab.text = sp.getInt(Constants.SP_COUNT_EQUIP, 0).toString()
                    //长按重置
                    tab.view.setOnLongClickListener {
                        sharedEquipViewModel.reset.postValue(true)
                        return@setOnLongClickListener true
                    }
                    //点击回顶部
                    tab.view.setOnClickListener {
                        if (MainActivity.currentMainPage == position) {
                            EquipmentListFragment.list.smoothScrollToPosition(0)
                        }
                    }
                }
            }
        }.attach()

    }

    //配置共享元素动画
    private fun prepareTransitions() {

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                try {
                    if (names!!.isNotEmpty()) {
                        sharedElements ?: return
                        //角色列表
                        val vh =
                            CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                                MainActivity.currentCharaPosition
                            ) ?: return
                        val v0 = vh.itemView
                        sharedElements[names[0]] = v0
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message ?: "")
                }
            }
        })
    }
}
