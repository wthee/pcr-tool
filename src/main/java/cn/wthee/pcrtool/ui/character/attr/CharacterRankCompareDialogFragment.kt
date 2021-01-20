package cn.wthee.pcrtool.ui.character.attr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.RankCompareAdapter
import cn.wthee.pcrtool.data.bean.getRankCompareList
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.databinding.FragmentCharacterRankCompareBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.RankSelectBtnsHelper
import cn.wthee.pcrtool.utils.ToolbarHelper
import cn.wthee.pcrtool.utils.getRankColor
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色 Rank 对比弹窗
 *
 * 页面布局 [FragmentCharacterRankCompareBinding]
 *
 * ViewModels [CharacterAttrViewModel]
 */
class CharacterRankCompareFragment : CommonBottomSheetDialogFragment(true) {

    private lateinit var binding: FragmentCharacterRankCompareBinding
    private val sharedAttrViewModel: CharacterAttrViewModel by activityViewModels {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }
    private var selRank0 = CharacterAttrFragment.maxRank
    private var selRank1 = CharacterAttrFragment.maxRank
    private var attr0 = Attr()
    private var attr1 = Attr()
    private lateinit var adapter: RankCompareAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterRankCompareBinding.inflate(inflater, container, false)
        MainScope().launch {
            attr0 = sharedAttrViewModel.getAttrs(
                CharacterAttrFragment.uid,
                CharacterAttrFragment.maxRank,
                CharacterAttrFragment.maxStar,
                CharacterAttrFragment.lv,
                CharacterAttrFragment.ueLv
            )
            attr1 = sharedAttrViewModel.getAttrs(
                CharacterAttrFragment.uid,
                CharacterAttrFragment.maxRank,
                CharacterAttrFragment.maxStar,
                CharacterAttrFragment.lv,
                CharacterAttrFragment.ueLv
            )

            adapter = RankCompareAdapter()

            binding.apply {
                ToolbarHelper(titleRankCompare).setCenterTitle(getString(R.string.rank_compare))
                //初始化数据
                rankCompare.adapter = adapter
                adapter.submitList(
                    getRankCompareList(attr0, attr1)
                )
                //列表头
                value0.text = getString(R.string.rank_pre, CharacterAttrFragment.maxRank)
                value0.setTextColor(getRankColor(CharacterAttrFragment.maxRank))
                value1.text = getString(R.string.rank_pre, CharacterAttrFragment.maxRank)
                value1.setTextColor(getRankColor(CharacterAttrFragment.maxRank))

                //选择按钮
                RankSelectBtnsHelper(binding.rankBtns0).apply {
                    initRank(selRank0)
                    setOnClickListener(
                        CharacterAttrFragment.maxRank,
                        object : RankSelectBtnsHelper.OnClickListener {
                            override fun onChange(rank: Int) {
                                MainScope().launch {
                                    attr0 = sharedAttrViewModel.getAttrs(
                                        CharacterAttrFragment.uid, rank,
                                        CharacterAttrFragment.selRarity,
                                        CharacterAttrFragment.lv,
                                        CharacterAttrFragment.ueLv
                                    )
                                    reload()
                                    value0.text = getString(R.string.rank_pre, rank)
                                    value0.setTextColor(getRankColor(rank))
                                }

                            }
                        })
                }
                RankSelectBtnsHelper(binding.rankBtns1).apply {
                    initRank(selRank1)
                    setOnClickListener(
                        CharacterAttrFragment.maxRank,
                        object : RankSelectBtnsHelper.OnClickListener {
                            override fun onChange(rank: Int) {
                                MainScope().launch {
                                    attr1 = sharedAttrViewModel.getAttrs(
                                        CharacterAttrFragment.uid, rank,
                                        CharacterAttrFragment.selRarity,
                                        CharacterAttrFragment.lv,
                                        CharacterAttrFragment.ueLv
                                    )
                                    reload()
                                    value1.text = getString(R.string.rank_pre, rank)
                                    value1.setTextColor(getRankColor(rank))
                                }
                            }
                        })
                }
            }

        }

        return binding.root
    }

    private fun reload() {
        adapter.submitList(getRankCompareList(attr0, attr1))
    }
}