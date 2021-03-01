package cn.wthee.pcrtool.ui.character.attr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.RankCompareAdapter
import cn.wthee.pcrtool.data.model.getRankCompareList
import cn.wthee.pcrtool.data.view.Attr
import cn.wthee.pcrtool.databinding.FragmentCharacterRankCompareBinding
import cn.wthee.pcrtool.utils.*
import coil.load
import kotlinx.coroutines.launch

/**
 * 角色 Rank 对比弹窗
 *
 * 页面布局 [FragmentCharacterRankCompareBinding]
 *
 * ViewModels [CharacterAttrViewModel]
 */
class CharacterRankCompareFragment : Fragment() {

    private val REQUEST_CODE_0 = 10
    private val REQUEST_CODE_1 = 11
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
        FabHelper.addBackFab(2)
        binding = FragmentCharacterRankCompareBinding.inflate(inflater, container, false)
        init()
        setListener()
        sharedAttrViewModel.selData.observe(viewLifecycleOwner) {
            update(it)
        }
        return binding.root
    }

    private fun update(it: MutableMap<String, Int>) {
        updateRankBtn()
        lifecycleScope.launch {
            it[Constants.RANK] = selRank0
            attr0 = sharedAttrViewModel.getAttrs(
                CharacterAttrFragment.uid,
                it
            )
            it[Constants.RANK] = selRank1
            attr1 = sharedAttrViewModel.getAttrs(
                CharacterAttrFragment.uid,
                it
            )
            adapter.submitList(getRankCompareList(attr0, attr1)) {
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun setListener() {
        binding.apply {
            //星级
            setRarity(CharacterAttrFragment.selData[Constants.RARITY]!!)
            //选择按钮
            value0.setOnClickListener {
                RankSelectDialogFragment(this@CharacterRankCompareFragment, REQUEST_CODE_0)
                    .getInstance(selRank0, 2, CharacterAttrFragment.maxRank)
                    .show(parentFragmentManager, "rank_select_0")
            }
            value1.setOnClickListener {
                RankSelectDialogFragment(this@CharacterRankCompareFragment, REQUEST_CODE_1)
                    .getInstance(selRank1, 2, CharacterAttrFragment.maxRank)
                    .show(parentFragmentManager, "rank_select_1")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_0 -> {
                data?.extras?.let {
                    selRank0 = it.getInt(Constants.SELECT_RANK)
                }
            }
            REQUEST_CODE_1 -> {
                data?.extras?.let {
                    selRank1 = it.getInt(Constants.SELECT_RANK)
                }
            }
        }
        postData()
    }

    private fun init() {
        binding.icon.load(CharacterAttrFragment.iconUrl) {
            error(R.drawable.unknown_gray)
            placeholder(R.drawable.unknown_gray)
        }
        adapter = RankCompareAdapter()
        binding.rankCompare.adapter = adapter
        binding.level.text = CharacterAttrFragment.selData[Constants.LEVEL]!!.toString()
        update(CharacterAttrFragment.selData)
    }

    private fun postData() {
        sharedAttrViewModel.selData.postValue(CharacterAttrFragment.selData)
    }

    private fun updateRankBtn() {
        binding.apply {
            value0.text = getRankText(selRank0)
            value0.setTextColor(getRankColor(selRank0))
            value1.text = getRankText(selRank1)
            value1.setTextColor(getRankColor(selRank1))
            title0.setTextColor(getRankColor(selRank0))
            title1.setTextColor(getRankColor(selRank1))
        }
    }

    //设置星级
    private fun setRarity(num: Int) {
        StarViewUtil.show(
            binding.root.context,
            binding.starts,
            num,
            CharacterAttrFragment.maxRarity,
            50,
            object : StarViewUtil.OnSelect {
                override fun select(index: Int) {
                    CharacterAttrFragment.selData[Constants.RARITY] = index
                    postData()
                }
            })
    }
}