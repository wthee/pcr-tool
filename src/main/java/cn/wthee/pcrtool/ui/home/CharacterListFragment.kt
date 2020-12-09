package cn.wthee.pcrtool.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.pageLevel
import cn.wthee.pcrtool.MainActivity.Companion.sortAsc
import cn.wthee.pcrtool.MainActivity.Companion.sortType
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterListAdapter
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.FragmentCharacterListBinding
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.enums.SortType
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import com.google.android.material.transition.Hold
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

/**
 * 角色列表
 */
class CharacterListFragment : Fragment() {

    companion object {
        lateinit var characterList: RecyclerView
        lateinit var listAdapter: CharacterListAdapter
        var characterFilterParams = FilterCharacter(
            true, 0, 0, "全部"
        )
        lateinit var handler: Handler
        lateinit var guilds: ArrayList<String>
        var r6Ids = listOf<Int>()
    }

    private lateinit var binding: FragmentCharacterListBinding
    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //从次页面至CharacterPagerFragment过渡开始
        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        //公会列表
        guilds = arrayListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            guilds.add("全部")
            val list = viewModel.getGuilds()
            list.forEach {
                guilds.add(it.guild_name)
            }
            guilds.add("？？？")
            r6Ids = viewModel.getR6Ids()
        }
        //加载数据
        init()
        //刷新
        binding.characterReset.apply {
            setProgressBackgroundColorSchemeColor(ResourcesUtil.getColor(R.color.colorWhite))
            setColorSchemeResources(R.color.colorPrimary)
            setOnRefreshListener {
                reset()
            }
        }
        //监听数据变化
        setObserve()
        //获取角色
        viewModel.getCharacters(sortType, sortAsc, "")
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
                    viewModel.reload.postValue(true)
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
        return binding.root
    }

    private fun reset() {
        characterFilterParams.initData()
        characterFilterParams.all = true
        sortType = SortType.SORT_DATE
        sortAsc = false
        viewModel.getCharacters(
            sortType,
            sortAsc, ""
        )
        binding.characterReset.isRefreshing = false
    }

    //加载数据
    private fun init() {
        listAdapter = CharacterListAdapter(this@CharacterListFragment)
        characterList = binding.characterList
        binding.characterList.apply {
            adapter = listAdapter
        }
    }


    //绑定observe
    private fun setObserve() {
        viewModel.apply {
            //角色数量
            if (!viewModel.characterCount.hasObservers()) {
                viewModel.characterCount.observe(viewLifecycleOwner, {
                    MainActivity.sp.edit {
                        putInt(Constants.SP_COUNT_CHARACTER, it)
                    }
                    MainPagerFragment.tabLayout.getTabAt(0)?.text = it.toString()
                    MainPagerFragment.tipText.visibility = if (it > 0) View.GONE else View.VISIBLE
                })
            }
            //角色信息
            if (!updateCharacter.hasObservers()) {
                updateCharacter.observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        @OptIn(ExperimentalCoroutinesApi::class)
                        viewModel.characters.collectLatest { data ->
                            listAdapter.submitData(data)
                            listAdapter.notifyDataSetChanged()
                            binding.loading.root.visibility = View.GONE
                        }
                    }
                })
            }
            //重置
            if (!reset.hasObservers()) {
                reset.observe(viewLifecycleOwner, {
                    reset()
                })
            }
            //重新加载
            if (!reload.hasObservers()) {
                reload.observe(viewLifecycleOwner, {
                    try {
                        findNavController().popBackStack(R.id.containerFragment, true)
                        findNavController().navigate(R.id.containerFragment)
                        pageLevel = 0
                        MainActivity.fabMain.setImageResource(R.drawable.ic_function)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, e.message.toString())
                    }
                })
            }
        }
    }

}
