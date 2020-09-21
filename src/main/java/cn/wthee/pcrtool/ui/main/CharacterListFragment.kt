package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sortAsc
import cn.wthee.pcrtool.MainActivity.Companion.sortType
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterAdapter
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.databinding.FragmentCharacterListBinding
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import com.google.android.material.transition.Hold
import kotlinx.coroutines.launch


class CharacterListFragment : Fragment() {

    companion object {
        lateinit var characterList: RecyclerView
        lateinit var listAdapter: CharacterAdapter
        var characterfilterParams = FilterCharacter(
            true, 0, 0, "全部"
        )
        lateinit var handler: Handler
        lateinit var guilds: ArrayList<String>
    }

    private lateinit var binding: FragmentCharacterListBinding
    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        binding.layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
        //公会列表
        guilds = arrayListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            guilds.add("全部")
            viewModel.getGuilds().forEach {
                guilds.add(it.guild_name)
            }
            guilds.add("？？？")
        }
        //加载数据
        init()
        //监听数据变化
        setObserve()
        //控件监听
        setListener()
        viewModel.getCharacters(sortType, sortAsc, "")
        //接收消息
        handler = Handler(Handler.Callback {
            when (it.what) {
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
                            override fun onButtonOperateClick(dialog: AlertDialog) {
                                //强制更新数据库
                                DatabaseUpdateHelper.forceUpdate()
                                ToastUtil.short(Constants.NOTICE_TOAST_TITLE_DB_DOWNLOAD)
                                dialog.dismiss()
                            }

                            override fun onButtonOkClick(dialog: AlertDialog) {
                                dialog.dismiss()
                            }
                        }
                    ).show()
                }
                1 -> {
                    viewModel.reload.postValue(true)
                }
                2 -> {
                    val layout = LayoutWarnDialogBinding.inflate(layoutInflater)
                    //弹窗
                    DialogUtil.create(
                        requireContext(),
                        layout,
                        "版本切换",
                        "版本切换完成，数据将在下次打开APP时更新~",
                        "关闭应用",
                        "暂不关闭",
                        object : DialogListener {
                            override fun onButtonOperateClick(dialog: AlertDialog) {
                                requireActivity().finish()
                            }

                            override fun onButtonOkClick(dialog: AlertDialog) {
                                dialog.dismiss()
                            }
                        }
                    ).show()
                }
            }

            return@Callback true
        })
        return binding.root
    }

    //加载数据
    private fun init() {
        listAdapter = CharacterAdapter(this@CharacterListFragment)
        characterList = binding.characterList
        binding.characterList.apply {
            adapter = listAdapter

        }
    }


    //绑定observe
    private fun setObserve() {
        viewModel.apply {
            //角色
            if (!characters.hasObservers()) {
                characters.observe(viewLifecycleOwner, Observer { data ->
                    if (data != null && data.isNotEmpty()) {
                        MainPagerFragment.tipText.visibility = View.GONE
                        listAdapter.submitList(data) {
                            listAdapter.filter.filter(characterfilterParams.toJsonString())
                            sp.edit {
                                putInt(Constants.SP_COUNT_CHARACTER, data.size)
                            }
                            characterList.scrollToPosition(0)
                            MainPagerFragment.tabLayout.getTabAt(0)?.text = data.size.toString()
                        }
                    } else {
                        MainPagerFragment.tipText.visibility = View.VISIBLE
                    }
                    isLoading.postValue(false)
                    refresh.postValue(false)
                })
            }
            //刷新
            if (!refresh.hasObservers()) {
                refresh.observe(viewLifecycleOwner, Observer {
                    binding.layoutRefresh.isRefreshing = it
                })
            }
            //加载
            if (!isLoading.hasObservers()) {
                isLoading.observe(viewLifecycleOwner, Observer {
                    binding.progress.visibility = if (it) View.VISIBLE else View.GONE
                })
            }
            //重新加载
            if (!reload.hasObservers()) {
                reload.observe(viewLifecycleOwner, Observer {
                    try {
                        findNavController().popBackStack(R.id.containerFragment, true)
                        findNavController().navigate(R.id.containerFragment)
                        MainActivity.isHome = true
                        MainActivity.fabMain.setImageResource(R.drawable.ic_function)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, e.message.toString())
                    }
                })
            }
        }
    }

    //控件监听
    private fun setListener() {
        binding.apply {
            //下拉刷新
            layoutRefresh.setOnRefreshListener {
                characterfilterParams.initData()
                characterfilterParams.all = true
                viewModel.getCharacters(sortType, sortAsc, "")
                layoutRefresh.isRefreshing = false
            }
        }
    }
}
