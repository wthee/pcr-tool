package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentPromotionAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPromotionBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import kotlin.math.round


private const val UNIT_ID = "id"
private const val UNIT_NAME = "name"
private const val UNIT_COMMENT = "comment"


class PromotionFragment : Fragment() {

    companion object {

        fun getInstance(param1: Int, param2: String, param3: String) =
            PromotionFragment().apply {
                arguments = Bundle().apply {
                    putInt(UNIT_ID, param1)
                    putString(UNIT_NAME, param2)
                    putString(UNIT_COMMENT, param3)
                }
            }
    }

    private var unitId: Int = 0
    private var unitName = ""
    private var unitComment = ""
    private lateinit var binding: FragmentCharacterPromotionBinding
    private lateinit var adapter: EquipmentPromotionAdapter
    private var selectRank = 2
    private var selRatity = 1
    private var lv = 85
    private lateinit var viewModel: CharacterPromotionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            unitId = it.getInt(UNIT_ID)
            unitName = it.getString(UNIT_NAME).toString()
            unitComment = it.getString(UNIT_COMMENT).toString()
        }
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterPromotionBinding.inflate(inflater, container, false)
        binding.apply {
            name.text = unitName
            comment.text = unitComment
            level.text = SpannableStringBuilder("85")
            //改变等级
            level.addTextChangedListener {
                val str = it?.toString() ?: "85"
                lv = if (str.isNotEmpty()) str.toInt() else {
                    level.text = SpannableStringBuilder("85")
                    85
                }
                viewModel.getCharacterInfo(unitId, selectRank, selRatity, lv)
            }

        }

        //获取viewModel
        viewModel = InjectorUtil.providePromotionViewModelFactory()
            .create(CharacterPromotionViewModel::class.java)
        //延迟加载
        MainScope().launch {
            delay(resources.getInteger(R.integer.delay).toLong())
            viewModel.getMaxRankAndRarity(unitId)
        }

        //数据监听
        setObserve()
        return binding.root
    }

    private fun setObserve() {
        //角色最大Rank
        viewModel.maxRankAndRarity.observe(viewLifecycleOwner, Observer { r ->
            selectRank = r[0]
            selRatity = r[1]
            viewModel.getCharacterInfo(unitId, selectRank, selRatity, lv)

            binding.apply {
                rank.text = selectRank.toString()
                rarity.text = selRatity.toString()
                rankAdd.setOnClickListener {
                    if (selectRank != r[0]) {
                        selectRank++
                        rank.text = selectRank.toString()
                        viewModel.getCharacterInfo(unitId, selectRank, selRatity, lv)
                    }
                }
                rankReduce.setOnClickListener {
                    if (selectRank != Constants.CHARACTER_MIN_RANK) {
                        selectRank--
                        rank.text = selectRank.toString()
                        viewModel.getCharacterInfo(unitId, selectRank, selRatity, lv)
                    }
                }
                rarityAdd.setOnClickListener {
                    if (selRatity != r[1]) {
                        selRatity++
                        rarity.text = selRatity.toString()
                        viewModel.getCharacterInfo(unitId, selectRank, selRatity, lv)
                    }
                }
                rarityReduce.setOnClickListener {
                    if (selRatity != 1) {
                        selRatity--
                        rarity.text = selRatity.toString()
                        viewModel.getCharacterInfo(unitId, selectRank, selRatity, lv)
                    }
                }
            }
        })
        //角色装备
        viewModel.equipments.observe(viewLifecycleOwner, Observer {
            binding.apply {
                adapter = EquipmentPromotionAdapter()
                recycler.adapter = adapter
            }
            adapter.submitList(it)
        })
        //角色属性
        viewModel.sumInfo.observe(viewLifecycleOwner, Observer {
            binding.apply {
                hp.text = getStr(it.hp)
                atk.text = getStr(it.atk)
                magicStr.text = getStr(it.magicStr)
                def.text = getStr(it.def)
                magicDef.text = getStr(it.magicDef)
                pCritical.text = getStr(it.physicalCritical)
                dodge.text = getStr(it.dodge)
                mCritical.text = getStr(it.magicCritical)
                hpRecovery.text = getStr(it.waveHpRecovery)
                tpRecovery.text = getStr(it.waveEnergyRecovery)
                lifeSteal.text = getStr(it.lifeSteal)
                hpRecoveryRate.text = getStr(it.hpRecoveryRate)
                energyRecoveryRate.text = getStr(it.energyRecoveryRate)
                energyReduceRate.text = getStr(it.energyReduceRate)
                accuracy.text = getStr(it.accuracy)
            }
        })
    }

    private fun getStr(d: Double): String = round(d).toInt().toString()


}