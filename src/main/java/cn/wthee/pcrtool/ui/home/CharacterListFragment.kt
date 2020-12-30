package cn.wthee.pcrtool.ui.home

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
import cn.wthee.pcrtool.MainActivity.Companion.pageLevel
import cn.wthee.pcrtool.MainActivity.Companion.sortAsc
import cn.wthee.pcrtool.MainActivity.Companion.sortType
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
    }

    private lateinit var binding: FragmentCharacterListBinding
    private lateinit var listAdapter: CharacterListAdapter
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
        //加载数据
        init()
        //监听数据变化
        setObserve()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        try {
            pageLevel = 0
            MainActivity.canClick = true
            //刷新收藏
//            val vh = characterList.findViewHolderForAdapterPosition(
//                MainActivity.currentCharaPosition
//            )?.itemView?.findViewById<MaterialTextView>(R.id.name)
//            val color = if (CharacterBasicInfoFragment.isLoved)
//                ResourcesUtil.getColor(R.color.colorPrimary)
//            else
//                ResourcesUtil.getColor(R.color.text)
//            vh?.setTextColor(color)

        } catch (e: java.lang.Exception) {
        }
    }


    private fun reset() {
        characterFilterParams.initData()
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
        //toolbar
        ToolbarUtil(binding.toolBar).setMainToolbar(getString(R.string.app_name))
        //获取角色
        viewModel.getCharacters(sortType, sortAsc, "")
        listAdapter = CharacterListAdapter(this@CharacterListFragment)
        binding.characterList.adapter = listAdapter
        //刷新
        binding.characterReset.apply {
            setProgressBackgroundColorSchemeColor(ResourcesUtil.getColor(R.color.colorWhite))
            setColorSchemeResources(R.color.colorPrimary)
            setOnRefreshListener {
                reset()
            }
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
