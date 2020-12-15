package cn.wthee.pcrtool.ui.detail.character.attr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterAttrAdapter
import cn.wthee.pcrtool.adapter.EquipmentAttrAdapter
import cn.wthee.pcrtool.data.db.view.all
import cn.wthee.pcrtool.data.db.view.allNotZero
import cn.wthee.pcrtool.databinding.FragmentCharacterAttrInfoBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterPagerFragment
import cn.wthee.pcrtool.ui.detail.character.CharacterPagerFragment.Companion.r6Id
import cn.wthee.pcrtool.ui.detail.character.CharacterRankCompareFragment
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.ui.home.EquipmentViewModel
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.UID
import coil.load
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

/**
 * 角色属性页面
 */
class CharacterAttrFragment : Fragment() {

    companion object {
        fun getInstance(uid: Int): CharacterAttrFragment {
            val fragment = CharacterAttrFragment()
            val bundle = Bundle()
            bundle.putInt(UID, uid)
            fragment.arguments = bundle
            return fragment
        }

        var selRatity = 1
        var maxRank = 1
        var maxStar = 5
        var lv = 85
        var ueLv = 1
        var uid = 0
    }


    private lateinit var binding: FragmentCharacterAttrInfoBinding
    private lateinit var attrAdapter: CharacterAttrAdapter
    private var selRank = 2
    private var index = 0
    private var iconUrls = arrayListOf<String>()

    private val sharedEquipViewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }
    private val characterAttrViewModel =
        InjectorUtil.provideCharacterAttrViewModelFactory()
            .create(CharacterAttrViewModel::class.java)

    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = CharacterPagerFragment.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterAttrInfoBinding.inflate(inflater, container, false)
        //点击事件
        setListener()
        //数据监听
        setObserve()
        iconUrls = CharacterIdUtil.getAllIconUrl(uid, r6Id)
        //加载icon
        loadIcon(iconUrls[index])
        characterAttrViewModel.getMaxRankAndRarity(uid)
        return binding.root
    }

    private fun loadIcon(url: String) {
        binding.icon.load(url) {
            error(R.drawable.unknown_gray)
            placeholder(R.drawable.unknown_gray)
        }
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            //头像点击查看掉落
            icon.setOnClickListener {
                lifecycleScope.launch {
                    if (sharedCharacterViewModel.getDrops(uid).isNotEmpty()) {
                        CharacterDropDialogFragment.getInstance(uid).show(
                            parentFragmentManager, "character_drop"
                        )
                    } else {
                        ToastUtil.short("无掉落信息~")
                    }
                }
            }
            //长按更换
            icon.setOnLongClickListener {
                index = (++index) % iconUrls.size
                loadIcon(iconUrls[index])
                return@setOnLongClickListener true
            }
            //等级滑动条
            levelSeekBar.addOnSliderTouchListener(object :
                Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    loadData()
                }

            })
            levelSeekBar.addOnChangeListener { slider, _, _ ->
                lv = slider.value.toInt()
                level.text = lv.toString()
            }
            //专武等级滑动
            uniqueEquip.ueLvSeekBar.addOnSliderTouchListener(object :
                Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    loadData()
                    sharedEquipViewModel.getUniqueEquipInfos(uid, ueLv)
                }
            })
            uniqueEquip.ueLvSeekBar.addOnChangeListener { slider, _, _ ->
                ueLv = slider.value.toInt()
                uniqueEquip.ueLv.text = getString(R.string.unique_equip_lv, ueLv)
            }
            //rank对比
            rankEquip.rankCompare.setOnClickListener {
                CharacterRankCompareFragment().show(parentFragmentManager, "rank_compare")
            }
        }
    }

    private fun setObserve() {
        //获取角色最大Rank后，加载数据
        characterAttrViewModel.maxData.observe(viewLifecycleOwner, { r ->
            maxRank = r[0]
            selRank = r[0]
            selRatity = r[1]
            maxStar = r[1]
            lv = r[2]
            ueLv = r[3]
            binding.apply {
                level.text = lv.toString()
                levelSeekBar.valueFrom = 1.0f
                levelSeekBar.valueTo = lv.toFloat()
                levelSeekBar.value = lv.toFloat()
                uniqueEquip.ueLvSeekBar.valueFrom = 1.0f
                uniqueEquip.ueLvSeekBar.valueTo = ueLv.toFloat()
                uniqueEquip.ueLvSeekBar.value = ueLv.toFloat()
                loadData(selRank)
                setRatity(selRatity)
                //rank 选择
                RankSelectBtnsHelper(binding.rankEquip.rankBtns).apply {
                    initRank(selRank)
                    setOnClickListener(maxRank, object : RankSelectBtnsHelper.OnClickListener {
                        override fun onChange(rank: Int) {
                            loadData(rank)
                        }
                    })
                }
            }
            //获取专武
            sharedEquipViewModel.getUniqueEquipInfos(uid, ueLv)
        })
        //专武
        sharedEquipViewModel.uniqueEquip.observe(viewLifecycleOwner, {
            binding.uniqueEquip.apply {
                if (it != null) {
                    binding.uniqueEquip.ueLv.visibility = View.VISIBLE
                    binding.uniqueEquip.ueLvSeekBar.visibility = View.VISIBLE
                    binding.uniqueEquip.root.visibility = View.VISIBLE
                    val picUrl = Constants.EQUIPMENT_URL + it.equipmentId + Constants.WEBP
                    itemPic.load(picUrl) {
                        placeholder(R.drawable.unknown_gray)
                        error(R.drawable.unknown_gray)
                    }
                    //描述
                    equipName.text = it.equipmentName
                    desc.text = it.getDesc()
                    //属性词条
                    val adapter = EquipmentAttrAdapter()
                    equipAttrs.adapter = adapter
                    adapter.submitList(it.attr.allNotZero())
                } else {
                    binding.uniqueEquip.root.visibility = View.GONE
                }

            }
        })
        //角色装备
        val equipPics = arrayListOf(
            binding.rankEquip.pic6,
            binding.rankEquip.pic5,
            binding.rankEquip.pic4,
            binding.rankEquip.pic3,
            binding.rankEquip.pic2,
            binding.rankEquip.pic1
        )
        characterAttrViewModel.equipments.observe(viewLifecycleOwner, {
            it.forEachIndexed { index, equip ->
                equipPics[index].apply {
                    //加载装备图片
                    val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                    this.load(picUrl) {
                        error(R.drawable.unknown_gray)
                        placeholder(R.drawable.unknown_gray)
                    }
                    //点击跳转
                    setOnClickListener {
                        if (equip.equipmentId != Constants.UNKNOW_EQUIP_ID) {
                            EquipmentDetailsDialogFragment.getInstance(equip)
                                .show(parentFragmentManager, "details")
                        }
                    }
                }
            }
        })
        //角色属性
        attrAdapter = CharacterAttrAdapter()
        binding.charcterAttrs.adapter = attrAdapter
        characterAttrViewModel.sumInfo.observe(viewLifecycleOwner, {
            attrAdapter.submitList(it.all()) {
                attrAdapter.notifyDataSetChanged()
            }
        })
        //角色剧情属性
        val storyAttrAdapter = CharacterAttrAdapter()
        binding.charcterStoryAttrs.adapter = storyAttrAdapter
        characterAttrViewModel.storyAttrs.observe(viewLifecycleOwner, {
            storyAttrAdapter.submitList(it.allNotZero()) {
                attrAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun loadData(rank: Int = selRank) {
        characterAttrViewModel.getCharacterInfo(uid, rank, selRatity, lv, ueLv)
    }


    //设置星级
    private fun setRatity(num: Int) {
        StarUtil.show(
            binding.root.context,
            binding.starts,
            num,
            maxStar,
            50,
            object : StarUtil.OnSelect {
                override fun select(index: Int) {
                    selRatity = index + 1
                    loadData()
                }
            })
    }

}
