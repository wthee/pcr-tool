package cn.wthee.pcrtool.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.MainActivity.Companion.canClick
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterListAdapter
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.databinding.FragmentCharacterListBinding
import cn.wthee.pcrtool.ui.common.CommonListFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 角色列表
 *
 * 页面布局 [FragmentCharacterListBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class CharacterListFragment : CommonListFragment() {

    companion object {
        var characterFilterParams = FilterCharacter(
            true, 0, 0, false, "全部"
        )
        var sortType = SortType.SORT_DATE
        var sortAsc = Constants.SORT_ASC
        var characterName = ""
        lateinit var guilds: ArrayList<String>
        var isPostponeEnterTransition = false
    }

    private var listAdapter = CharacterListAdapter(this)
    private lateinit var binding: FragmentCharacterListBinding
    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        //加载数据
        init()
        setListener()
        //监听数据变化
        setObserve()
        if (isPostponeEnterTransition) {
            postponeEnterTransition()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        canClick = true
    }

    private fun reset() {
        characterFilterParams.initData()
        sortType = SortType.SORT_DATE
        sortAsc = false
        characterName = ""
        load()
    }

    //加载数据
    private fun init() {
        //toolbar
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.mipmap.ic_logo,
            getString(R.string.app_name)
        )
        lifecycleScope.launch {
            //公会列表
            guilds = arrayListOf()
            lifecycleScope.launch {
                guilds.add("全部")
                val list = viewModel.getGuilds()
                list.forEach {
                    guilds.add(it.guildName)
                }
                guilds.add("？？？")
            }
        }
        //获取角色
        load()
        binding.toolList.adapter = listAdapter
    }

    private fun load() {
        lifecycleScope.launch {
            //获取角色
            DataStoreUtil.get(Constants.SP_STAR_CHARACTER).collect { str ->
                val newStarIds = DataStoreUtil.fromJson<ArrayList<Int>>(str)
                characterFilterParams.starIds = newStarIds ?: arrayListOf()
                viewModel.getCharacters(
                    characterFilterParams,
                    sortType,
                    sortAsc,
                    characterName,
                    false
                )
            }
        }
    }

    private fun setListener() {
        //重置
        binding.characterCount.setOnLongClickListener {
            reset()
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
                viewModel.characterCount.observe(viewLifecycleOwner) {
                    binding.characterCount.text = it.toString()
                }
            }
            //角色信息
            if (!updateCharacter.hasObservers()) {
                updateCharacter.observe(viewLifecycleOwner) {
                    lifecycleScope.launch {
                        @OptIn(ExperimentalCoroutinesApi::class)
                        viewModel.characters.collectLatest { data ->
                            listAdapter.submitData(data)
                        }
                    }
                }
            }
            //重置
            if (!reset.hasObservers()) {
                reset.observe(viewLifecycleOwner) {
                    reset()
                }
            }
        }
    }

}
