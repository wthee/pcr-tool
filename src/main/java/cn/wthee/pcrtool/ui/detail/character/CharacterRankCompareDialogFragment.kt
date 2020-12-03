package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapters.RankCompareAdapter
import cn.wthee.pcrtool.data.model.getRankCompareList
import cn.wthee.pcrtool.data.view.Attr
import cn.wthee.pcrtool.data.view.Compare
import cn.wthee.pcrtool.data.view.all
import cn.wthee.pcrtool.databinding.FragmentCharacterRankCompareBinding
import cn.wthee.pcrtool.ui.common.CommonBasicDialogFragment
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.RankSelectBtnsHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class CharacterRankCompareFragment : CommonBasicDialogFragment() {

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
                //初始化数据
                rankCompare.adapter = adapter
                adapter.submitList(
                    getRankCompareList(
                        attr0.all(),
                        attr1.all(),
                        attr1.Compare(attr0)
                    )
                )
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
                                        CharacterAttrFragment.selRatity,
                                        CharacterAttrFragment.lv,
                                        CharacterAttrFragment.ueLv
                                    )
                                    reload()
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
                                        CharacterAttrFragment.selRatity,
                                        CharacterAttrFragment.lv,
                                        CharacterAttrFragment.ueLv
                                    )
                                    reload()
                                }
                            }
                        })
                }
            }

        }

        return binding.root
    }

    private fun reload() {
        adapter.submitList(getRankCompareList(attr0.all(), attr1.all(), attr1.Compare(attr0)))
    }
}