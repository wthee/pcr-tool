package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpCharacterAdapter
import cn.wthee.pcrtool.adapter.viewpager.PvpCharacterPagerAdapter
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getDefault
import cn.wthee.pcrtool.databinding.LayoutPvpSelectBinding
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.InjectorUtil
import com.google.android.material.tabs.TabLayoutMediator

/**
 * 竞技场角色选择
 *
 * [customize] -1：页面查询 0：自定义进攻方选择 1：自定义防守方选择
 *
 * 页面布局 [LayoutPvpSelectBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class PvpSelectFragment(private val customize: Int = -1) : Fragment() {

    companion object {
        var selects = getDefault()
        var allCharecters = listOf<PvpCharacterData>()
        var character1 = listOf<PvpCharacterData>()
        var character2 = listOf<PvpCharacterData>()
        var character3 = listOf<PvpCharacterData>()
        lateinit var selectedAdapter: PvpCharacterAdapter
    }

    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private lateinit var binding: LayoutPvpSelectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutPvpSelectBinding.inflate(layoutInflater, container, false)
        if (customize == 0) {
            selects = PvpLikedCusFragment.atkSelected
        }
        if (customize == 1) {
            selects = PvpLikedCusFragment.defSelected
        }
        //已选择角色
        loadDefault()
        //角色页面 绑定tab viewpager
        viewModel.getAllCharacter()
        viewModel.allPvpCharacterData.observe(viewLifecycleOwner) { data ->
            allCharecters = data
            character1 = data.filter {
                it.position in 0..299
            }
            character2 = data.filter {
                it.position in 300..599
            }
            character3 = data.filter {
                it.position in 600..9999
            }
            setPager()
        }
        return binding.root
    }


    //已选择角色
    private fun loadDefault() {
        selectedAdapter = PvpCharacterAdapter(isFloatWindow = false, isPager = false)
        binding.selectCharacters.adapter = selectedAdapter
        selectedAdapter.submitList(selects)
        selectedAdapter.notifyDataSetChanged()
    }


    private fun setPager() {
        binding.pvpPager.offscreenPageLimit = 3
        binding.pvpPager.adapter = PvpCharacterPagerAdapter(requireActivity(), false)
        TabLayoutMediator(
            binding.tablayoutPosition,
            binding.pvpPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.position_1)
                }
                1 -> {
                    tab.text = getString(R.string.position_2)
                }
                2 -> {
                    tab.text = getString(R.string.position_3)
                }
            }
        }.attach()
    }

}