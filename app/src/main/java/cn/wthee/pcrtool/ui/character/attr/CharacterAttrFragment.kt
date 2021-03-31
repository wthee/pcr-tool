package cn.wthee.pcrtool.ui.character.attr

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.AttrAdapter
import cn.wthee.pcrtool.data.view.all
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.databinding.FragmentCharacterAttrInfoBinding
import cn.wthee.pcrtool.ui.character.attr.CharacterAttrFragment.Companion.uid
import cn.wthee.pcrtool.ui.tool.equip.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import coil.load
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色面板属性页面
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterAttrInfoBinding]
 *
 * ViewModels [CharacterViewModel] [EquipmentViewModel] [CharacterAttrViewModel]
 */
class CharacterAttrFragment : Fragment() {

    companion object {

        fun getInstance(uid: Int) = CharacterAttrFragment().apply {
            arguments = Bundle().apply {
                putInt(UID, uid)
            }
        }

        var maxRank = 1
        var maxRarity = 5
        var maxLv = 5
        var uid = 0
        var selData = mutableMapOf<String, Int>()
        var iconUrl = ""
    }

    private val REQUEST_CODE = 0
    private val REQUEST_CODE_LEVEL = 1
    private val REQUEST_CODE_UE_LEVEL = 2
    private lateinit var binding: FragmentCharacterAttrInfoBinding
    private lateinit var attrAdapter: AttrAdapter

    private val sharedEquipViewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    private val characterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }

    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterAttrInfoBinding.inflate(inflater, container, false)
        init()
        //点击事件
        setListener()
        //数据监听
        setObserve()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> data?.extras?.let {
                selData[Constants.RANK] = it.getInt(Constants.SELECT_RANK)
            }
            REQUEST_CODE_LEVEL -> data?.extras?.let {
                selData[Constants.LEVEL] = it.getInt(Constants.SELECT_LEVEL)
            }
            REQUEST_CODE_UE_LEVEL -> data?.extras?.let {
                selData[Constants.UNIQUE_EQUIP_LEVEL] = it.getInt(Constants.SELECT_LEVEL)
                sharedEquipViewModel.getUniqueEquipInfos(
                    uid,
                    selData[Constants.UNIQUE_EQUIP_LEVEL]!!
                )
            }
        }
        loadData()
    }

    private fun init() {
        lifecycleScope.launch {
            iconUrl = CharacterIdUtil.getMaxIconUrl(
                uid, sharedCharacterViewModel.getR6Ids().contains(
                    uid
                )
            )
        }
        attrAdapter = AttrAdapter()
        binding.charcterAttrs.adapter = attrAdapter

        characterAttrViewModel.getMaxRankAndRarity(uid)
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            //查看装备统计
            rankEquip.rankCompare.setOnClickListener {
                findNavController().navigate(R.id.action_characterPagerFragment_to_characterRankCompareFragment)
            }
            //专武分享
            uniqueShare.setOnClickListener {
                //分享图片
                ShareIntentUtil.image(
                    requireActivity(),
                    uniqueEquip.root,
                    "ue_${uid}_lv_${uniqueEquip.ueLv.text}.png"
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setObserve() {
        //获取属性，重新加载
        characterAttrViewModel.selData.observe(viewLifecycleOwner) {
            updateText()
            characterAttrViewModel.getCharacterInfo(uid, selData)
        }
        //获取角色最大Rank后，加载数据
        characterAttrViewModel.maxData.observe(viewLifecycleOwner) { maxData ->
            val ueLv = maxData[Constants.UNIQUE_EQUIP_LEVEL]!!
            val lv = maxData[Constants.LEVEL]!!
            maxLv = lv
            maxRank = maxData[Constants.RANK] ?: 11
            maxRarity = maxData[Constants.RARITY] ?: 5
            selData = maxData
            updateText()
            //等级选择
            binding.level.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    LevelSelectDialogFragment(
                        this@CharacterAttrFragment,
                        REQUEST_CODE_LEVEL,
                        event.rawY.toInt()
                    ).getInstance(selData[Constants.LEVEL] ?: maxLv, maxLv)
                        .show(parentFragmentManager, "level_select")
                }
                return@setOnTouchListener true
            }
            //专武等级选择
            binding.uniqueEquip.layoutEquip.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    LevelSelectDialogFragment(
                        this@CharacterAttrFragment,
                        REQUEST_CODE_UE_LEVEL,
                        event.rawY.toInt()
                    ).getInstance(selData[Constants.UNIQUE_EQUIP_LEVEL] ?: ueLv, ueLv)
                        .show(parentFragmentManager, "ue_level_select")
                }
                return@setOnTouchListener true
            }
            //显示数据
            binding.apply {
                level.text = lv.toString()
                loadData()
                setRarity(maxRarity)
                //rank 选择
                rankEquip.rankBtn.setOnClickListener {
                    RankSelectDialogFragment(this@CharacterAttrFragment, REQUEST_CODE)
                        .getInstance(selData[Constants.RANK]!!, 2, maxRank)
                        .show(parentFragmentManager, "rank_select")
                }
            }
            //获取专武
            sharedEquipViewModel.getUniqueEquipInfos(uid, ueLv)
        }
        //专武
        //属性词条
        val adapter = AttrAdapter(2f, 3f)
        binding.uniqueEquip.equipAttrs.adapter = adapter
        sharedEquipViewModel.uniqueEquip.observe(viewLifecycleOwner) {
            binding.uniqueEquip.apply {
                if (it != null) {
                    binding.uniqueEquip.root.visibility = View.VISIBLE
                    val picUrl = Constants.EQUIPMENT_URL + it.equipmentId + Constants.WEBP
                    itemPic.load(picUrl) {
                        placeholder(R.drawable.unknown_gray)
                        error(R.drawable.unknown_gray)
                    }
                    //描述
                    equipName.text = it.equipmentName
                    desc.text = it.getDesc()
                    adapter.submitList(it.attr.allNotZero())
                } else {
                    binding.uniqueEquip.root.visibility = View.GONE
                }

            }
        }
        //角色装备
        val equipPics = arrayListOf(
            binding.rankEquip.pic6,
            binding.rankEquip.pic5,
            binding.rankEquip.pic4,
            binding.rankEquip.pic3,
            binding.rankEquip.pic2,
            binding.rankEquip.pic1
        )
        characterAttrViewModel.equipments.observe(viewLifecycleOwner) {
            it.forEachIndexed { index, equip ->
                equipPics[index].apply {
                    //加载装备图片
                    try {
                        lifecycleScope.launch {
                            val picUrl =
                                Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                            this@apply.load(picUrl) {
                                error(R.drawable.unknown_gray)
                                placeholder(R.drawable.unknown_gray)
                            }
                        }
                    } catch (e: Exception) {
                        MainScope().launch {
                            UMCrash.generateCustomLog(
                                e,
                                Constants.EXCEPTION_LOAD_PIC + equip.equipmentId
                            )
                        }
                        this.load(R.drawable.unknown_gray)
                    }

                    //点击跳转
                    setOnClickListener {
                        if (equip.equipmentId != Constants.UNKNOWN_EQUIP_ID) {
                            EquipmentDetailsDialogFragment.getInstance(equip)
                                .show(parentFragmentManager, "details")
                        }
                    }
                }
            }
        }
        //角色属性
        characterAttrViewModel.sumInfo.observe(viewLifecycleOwner) {
            attrAdapter.submitList(it.all()) {
                attrAdapter.notifyDataSetChanged()
            }
        }
        //角色剧情属性
        val storyAttrAdapter = AttrAdapter()
        binding.charcterStoryAttrs.adapter = storyAttrAdapter
        characterAttrViewModel.storyAttrs.observe(viewLifecycleOwner) {
            storyAttrAdapter.submitList(it.allNotZero())
        }
    }

    //获取角色属性信息
    private fun loadData() {
        characterAttrViewModel.selData.postValue(selData)
    }


    //修改文本
    private fun updateText() {
        binding.rankEquip.rankBtn.apply {
            val selRank = selData[Constants.RANK]!!
            text = getFormatText(selRank)
            setTextColor(getRankColor(selRank))
        }
        binding.level.text = selData[Constants.LEVEL]!!.toString()
        binding.uniqueEquip.ueLv.text = selData[Constants.UNIQUE_EQUIP_LEVEL]!!.toString()
    }

    //设置星级
    private fun setRarity(num: Int) {
        StarViewUtil.show(
            binding.root.context,
            binding.stars,
            num,
            maxRarity,
            50,
            object : StarViewUtil.OnSelect {
                override fun select(index: Int) {
                    selData[Constants.RARITY] = index + 1
                    loadData()
                }
            })
    }

}
