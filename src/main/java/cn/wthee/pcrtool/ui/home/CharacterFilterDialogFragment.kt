package cn.wthee.pcrtool.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.view.forEachIndexed
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentFilterCharacterBinding
import cn.wthee.pcrtool.databinding.LayoutChipBinding
import cn.wthee.pcrtool.enums.SortType
import cn.wthee.pcrtool.ui.common.CommonDialogFragment
import cn.wthee.pcrtool.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * 角色筛选弹窗
 *
 * 页面布局 [FragmentFilterCharacterBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class CharacterFilterDialogFragment : CommonDialogFragment() {

    private lateinit var binding: FragmentFilterCharacterBinding
    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterCharacterBinding.inflate(layoutInflater, container, false)
        //筛选
        val chips = binding.chipsGuild
        initFilter(chips, binding)
        binding.reset.setOnClickListener {
            viewModel.reset.postValue(true)
            dialog?.dismiss()
        }
        binding.next.setOnClickListener {
            filterData(binding)
            dialog?.dismiss()
        }
        return binding.root
    }

    private fun filterData(binding: FragmentFilterCharacterBinding) {
        //排序选项
        CharacterListFragment.sortType = when (binding.sortTypeChips.checkedChipId) {
            R.id.sort_chip_0 -> SortType.SORT_DATE
            R.id.sort_chip_1 -> SortType.SORT_AGE
            R.id.sort_chip_2 -> SortType.SORT_HEIGHT
            R.id.sort_chip_3 -> SortType.SORT_WEIGHT
            R.id.sort_chip_4 -> SortType.SORT_POSITION
            else -> SortType.SORT_DATE
        }
        CharacterListFragment.sortAsc = binding.ascChips.checkedChipId == R.id.asc
        //收藏
        CharacterListFragment.characterFilterParams.all =
            when (binding.chipsStars.checkedChipId) {
                R.id.star_0 -> true
                R.id.star_1 -> false
                else -> true
            }
        //星级
        CharacterListFragment.characterFilterParams.r6 =
            when (binding.chipsRaritys.checkedChipId) {
                R.id.rarity_0 -> false
                R.id.rarity_1 -> true
                else -> false
            }
        //位置
        CharacterListFragment.characterFilterParams.positon =
            when (binding.chipsPosition.checkedChipId) {
                R.id.position_chip_1 -> 1
                R.id.position_chip_2 -> 2
                R.id.position_chip_3 -> 3
                else -> 0
            }
        //攻击类型
        CharacterListFragment.characterFilterParams.atk =
            when (binding.chipsAtk.checkedChipId) {
                R.id.atk_chip_1 -> 1
                R.id.atk_chip_2 -> 2
                else -> 0
            }
        //公会筛选
        val chip =
            binding.root.findViewById<Chip>(binding.chipsGuild.checkedChipId)
        CharacterListFragment.characterFilterParams.guild =
            chip.text.toString()
        //筛选
        CharacterListFragment.characterName = binding.searchInput.text.toString()
        viewModel.getCharacters(
            CharacterListFragment.sortType,
            CharacterListFragment.sortAsc, CharacterListFragment.characterName
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFilter(
        chips: ChipGroup,
        binding: FragmentFilterCharacterBinding
    ) {
        //添加公会信息
        CharacterListFragment.guilds.forEachIndexed { _, guild ->
            val chip = LayoutChipBinding.inflate(layoutInflater).root
            chip.text = guild
            chip.isCheckable = true
            chip.isClickable = true
            chips.addView(chip)
            if (CharacterListFragment.characterFilterParams.guild == guild) {
                chip.isChecked = true
            }
        }
        //排序类型
        when (CharacterListFragment.sortType) {
            SortType.SORT_DATE -> binding.sortChip0.isChecked = true
            SortType.SORT_AGE -> binding.sortChip1.isChecked = true
            SortType.SORT_HEIGHT -> binding.sortChip2.isChecked = true
            SortType.SORT_POSITION -> binding.sortChip3.isChecked = true
            SortType.SORT_WEIGHT -> binding.sortChip4.isChecked = true
        }
        //排序规则
        if (CharacterListFragment.sortAsc) binding.asc.isChecked =
            true else binding.desc.isChecked = true
        //收藏初始
        binding.chipsStars.forEachIndexed { index, view ->
            val chip = view as Chip
            chip.isChecked =
                (CharacterListFragment.characterFilterParams.all
                        && index == 0)
                        || (!CharacterListFragment.characterFilterParams.all
                        && index == 1)
        }
        //星级初始
        binding.chipsRaritys.forEachIndexed { index, view ->
            val chip = view as Chip
            chip.isChecked =
                (CharacterListFragment.characterFilterParams.r6
                        && index == 1)
                        || (!CharacterListFragment.characterFilterParams.r6
                        && index == 0)
        }
        //位置初始
        binding.chipsPosition.forEachIndexed { index, view ->
            val chip = view as Chip
            chip.isChecked =
                CharacterListFragment.characterFilterParams.positon == index
        }
        //攻击类型初始
        binding.chipsAtk.forEachIndexed { index, view ->
            val chip = view as Chip
            chip.isChecked = CharacterListFragment.characterFilterParams.atk == index
        }
        //名字
        if (CharacterListFragment.characterName != "") {
            binding.searchInput.setText(CharacterListFragment.characterName)
        }
        //取消焦点
        binding.layoutFilter.setOnTouchListener { _, _ ->
            binding.searchInput.clearFocus()
            return@setOnTouchListener false
        }
    }
}