package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpCharacterAdapter
import cn.wthee.pcrtool.adapter.viewpager.PvpCharacterPagerAdapter
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getDefault
import cn.wthee.pcrtool.databinding.LayoutPvpSelectBinding
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.InjectorUtil
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class PvpSelectFragment(private val customize: Int = -1) : Fragment() {

    companion object {
        var selects = getDefault()
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
        if (customize != -1) binding.pcrfan.visibility = View.GONE
        if (customize == 0) {
            selects = PvpLikedCusFragment.atkSelected
        }
        if (customize == 1) {
            selects = PvpLikedCusFragment.defSelected
        }
        //已选择角色
        loadDefault()
        //角色页面 绑定tab viewpager
        lifecycleScope.launch {
            character1 = viewModel.getCharacterByPosition(1)
            character2 = viewModel.getCharacterByPosition(2)
            character3 = viewModel.getCharacterByPosition(3)
            setPager()
        }

        binding.apply {
            pcrfan.setOnClickListener {
                //从其他浏览器打开
                BrowserUtil.open(requireContext(), getString(R.string.url_pcrdfans_com))
            }
        }
        return binding.root
    }


    //已选择角色
    private fun loadDefault() {
        selectedAdapter = PvpCharacterAdapter(false)
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