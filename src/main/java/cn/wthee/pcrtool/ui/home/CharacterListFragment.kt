package cn.wthee.pcrtool.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity.Companion.canClick
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterListAdapter
import cn.wthee.pcrtool.data.bean.FilterCharacter
import cn.wthee.pcrtool.databinding.FragmentCharacterListBinding
import cn.wthee.pcrtool.enums.SortType
import cn.wthee.pcrtool.utils.*
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
        var isPostponeEnterTransition = false
        lateinit var motionLayout: MotionLayout
        lateinit var characterList: RecyclerView

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
        viewModel.getCharacters(
            sortType,
            sortAsc, characterName
        )
    }

    //加载数据
    private fun init() {
        motionLayout = binding.root
        characterList = binding.pagerList
        //toolbar
        ToolbarUtil(binding.toolBar).setMainToolbar(
            R.mipmap.ic_logo,
            getString(R.string.app_name)
        )
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
        binding.pagerList.adapter = listAdapter
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
                viewModel.characterCount.observe(viewLifecycleOwner, {
                    val sp = requireActivity().getSharedPreferences("main", Context.MODE_PRIVATE)
                    sp.edit {
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
        }
    }

}
