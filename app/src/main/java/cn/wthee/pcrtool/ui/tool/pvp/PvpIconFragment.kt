package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapter.PvpIconAdapter
import cn.wthee.pcrtool.adapter.PvpPositionAdapter
import cn.wthee.pcrtool.data.model.PvpPositionData
import cn.wthee.pcrtool.data.view.getDefault
import cn.wthee.pcrtool.databinding.LayoutPvpIconBinding
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.init


/**
 * 竞技场角色选择
 *
 * [customize] -1：页面查询 0：自定义进攻方选择 1：自定义防守方选择
 *
 * 页面布局 [LayoutPvpIconBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class PvpIconFragment(private val customize: Int = -1) : Fragment() {

    companion object {
        var selects = getDefault()
        var allCharacters = arrayListOf<PvpPositionData>()
        lateinit var selectedAdapter: PvpIconAdapter
    }

    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private lateinit var binding: LayoutPvpIconBinding
    private lateinit var adapter: PvpPositionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutPvpIconBinding.inflate(layoutInflater, container, false)

        initList()

        //角色页面 绑定tab viewpager
        viewModel.getAllCharacter()
        viewModel.allPvpCharacterData.observe(viewLifecycleOwner) { data ->
            val character1 = data.filter {
                it.position in 0..299
            }
            val character2 = data.filter {
                it.position in 300..599
            }
            val character3 = data.filter {
                it.position in 600..9999
            }
            allCharacters.clear()
            allCharacters.add(PvpPositionData(1, character1))
            allCharacters.add(PvpPositionData(2, character2))
            allCharacters.add(PvpPositionData(3, character3))
            adapter.submitList(allCharacters)
        }
        return binding.root
    }

    private fun initList() {
        if (customize == 0) {
            selects = PvpLikedCusFragment.atkSelected
        }
        if (customize == 1) {
            selects = PvpLikedCusFragment.defSelected
        }

        selectedAdapter = PvpIconAdapter(false)
        binding.selectCharacters.adapter = selectedAdapter
        selectedAdapter.submitList(selects)
        selectedAdapter.notifyDataSetChanged()
        adapter = PvpPositionAdapter(false)
        binding.listAll.adapter = adapter

        //指示器
        binding.indicator.init(binding.listAll, false)

    }

}