package cn.wthee.pcrtool.ui.character.attr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapter.EquipmentMaterialAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterRankEquipBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.ui.tool.equip.EquipmentViewModel
import cn.wthee.pcrtool.utils.*

/**
 * 角色提升 Rank 所需装备页面
 *
 * 页面布局 [FragmentCharacterRankEquipBinding]
 *
 * ViewModels [EquipmentViewModel]
 */
class CharacterRankRangeEquipFragment : CommonBottomSheetDialogFragment() {

    companion object {
        fun getInstance(uid: Int) =
            CharacterRankRangeEquipFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.UID, uid)
                }
            }
    }

    private val REQUEST_CODE_0 = 20
    private val REQUEST_CODE_1 = 21
    private lateinit var binding: FragmentCharacterRankEquipBinding
    private val sharedEquipViewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }
    private var uid = -1
    private var startRank = 1
    private var endRank = CharacterAttrFragment.maxRank
    private lateinit var toolbarHelper: ToolbarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            uid = getInt(Constants.UID)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCharacterRankEquipBinding.inflate(inflater, container, false)
        init()
        setListener()
        load()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_0 -> {
                data?.extras?.let {
                    startRank = it.getInt(Constants.SELECT_RANK)
                }
            }
            REQUEST_CODE_1 -> {
                data?.extras?.let {
                    endRank = it.getInt(Constants.SELECT_RANK)
                }
            }
        }
        load()
    }

    private fun init() {
        val adapter = EquipmentMaterialAdapter(viewModel = sharedEquipViewModel)
        binding.listEquip.adapter = adapter
        ToolbarHelper(binding.toolHead).setCenterTitle("所需装备")
        sharedEquipViewModel.rankEquipMaterials.observe(viewLifecycleOwner) {
            adapter.submitList(it) {
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun setListener() {
        binding.apply {
            value0.setOnClickListener {
                RankSelectDialogFragment(this@CharacterRankRangeEquipFragment, REQUEST_CODE_0)
                    .getInstance(startRank, 1, endRank)
                    .show(requireFragmentManager(), "rank_select_0")
            }
            value1.setOnClickListener {
                RankSelectDialogFragment(this@CharacterRankRangeEquipFragment, REQUEST_CODE_1)
                    .getInstance(endRank, startRank, CharacterAttrFragment.maxRank)
                    .show(requireFragmentManager(), "rank_select_1")
            }
        }
    }

    private fun load() {
        sharedEquipViewModel.getEquipByRank(uid, startRank, endRank)
        binding.apply {
            value0.text = getRankText(startRank)
            value0.setTextColor(getRankColor(startRank))
            value1.text = getRankText(endRank)
            value1.setTextColor(getRankColor(endRank))
        }
    }
}