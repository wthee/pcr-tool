package cn.wthee.pcrtool.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.canClick
import cn.wthee.pcrtool.MainActivity.Companion.pageLevel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterListAdapter
import cn.wthee.pcrtool.data.bean.FilterCharacter
import cn.wthee.pcrtool.databinding.FragmentCharacterListBinding
import cn.wthee.pcrtool.enums.SortType
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import com.google.android.material.transition.Hold
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 角色列表
 */
class CharacterListFragment : Fragment() {

    companion object {
        var characterFilterParams = FilterCharacter(
            true, 0, 0, false, "全部"
        )
        var sortType = SortType.SORT_DATE
        var sortAsc = Constants.SORT_ASC
        var characterName = ""
        lateinit var guilds: ArrayList<String>
        var r6Ids = listOf<Int>()
    }

    var listAdapter = CharacterListAdapter(this)
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
        Log.e("create", "FragmentCharacterListBinding")
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        //加载数据
        init()
        //监听数据变化
        setObserve()
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        canClick = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    private fun reset() {
        characterFilterParams.initData()
        sortType = SortType.SORT_DATE
        sortAsc = false
        characterName = ""
        viewModel.getCharacters(
            sortType,
            sortAsc, characterName
        )
    }

    //加载数据
    private fun init() {
        //toolbar
        ToolbarUtil(binding.toolBar).setMainToolbar(getString(R.string.app_name))
        //获取角色
        viewModel.getCharacters(sortType, sortAsc, characterName, false)
        lifecycleScope.launch {
            //公会列表
            guilds = arrayListOf()
            lifecycleScope.launch {
                guilds.add("全部")
                val list = viewModel.getGuilds()
                list.forEach {
                    guilds.add(it.guild_name)
                }
                guilds.add("？？？")
            }
            r6Ids = viewModel.getR6Ids()
        }
        binding.characterList.adapter = listAdapter
        //回到顶部
        binding.characterCount.setOnLongClickListener {
            binding.characterList.smoothScrollToPosition(0)
            return@setOnLongClickListener true
        }
        //筛选、搜索
        binding.characterCount.setOnClickListener {
            CharacterFilterDialogFragment().show(parentFragmentManager, "filter_character")
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
                    binding.characterCount.text = it.toString()
                })
            }
            //角色信息
            if (!updateCharacter.hasObservers()) {
                updateCharacter.observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        @OptIn(ExperimentalCoroutinesApi::class)
                        viewModel.characters.collectLatest { data ->
                            listAdapter.submitData(data)
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
                        if (it) {
                            requireActivity().recreate()
                            pageLevel = 0
                            MainActivity.fabMain.setImageResource(R.drawable.ic_function)
                        }
                        reload.postValue(false)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, e.message.toString())
                    }
                })
            }
        }
    }

}
