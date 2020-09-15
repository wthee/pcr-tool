package cn.wthee.pcrtool.ui.detail.character

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.canBack
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.data.model.entity.CharacterBasicInfo
import cn.wthee.pcrtool.data.model.getList
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.utils.*
import coil.Coil
import coil.load
import coil.metadata
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.slider.Slider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import kotlin.math.abs


class CharacterBasicInfoFragment : Fragment() {

    companion object {
        var isLoved = false
        fun getInstance(characterInfo: CharacterBasicInfo): CharacterBasicInfoFragment {
            val fragment = CharacterBasicInfoFragment()
            val bundle = Bundle()
            bundle.putSerializable("character", characterInfo)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var character: CharacterBasicInfo
    private lateinit var binding: FragmentCharacterBasicInfoBinding
    private lateinit var viewModel: CharacterPromotionViewModel
    private lateinit var attrAdapter: CharacterAttrAdapter
    private var selRank = 2
    private var selRatity = 1
    private var maxStar = 5
    private var lv = 85

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            character = it.getSerializable("character") as CharacterBasicInfo
        }
        isLoved = sp.getBoolean(character.id.toString(), false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterBasicInfoBinding.inflate(inflater, container, false)
        //设置共享元素
        binding.root.transitionName = "item_${character.id}"
        //开始动画
        ObjectAnimatorHelper.enter(object : ObjectAnimatorHelper.OnAnimatorListener {
            override fun prev(view: View) {
                view.visibility = View.GONE
            }

            override fun start(view: View) {
                view.visibility = View.VISIBLE
            }

            override fun end(view: View) {
                viewModel.getMaxRankAndRarity(character.id)
                CharacterSkillFragment.viewModel.getCharacterSkills(character.id)
            }
        }, binding.fabLoveCbi, binding.basicInfo, binding.promotion.root)

        //加载图片
        loadImages()
        //点击事件
        setListener()
        //初始化数据
        setData()
        setHasOptionsMenu(true)
        //获取viewModel
        viewModel = InjectorUtil.providePromotionViewModelFactory()
            .create(CharacterPromotionViewModel::class.java)

        //数据监听
        setObserve()
        //初始收藏
        setLove(isLoved)
        return binding.root
    }


    //加载图片
    private fun loadImages() {
        //toolbar 背景
        val picUrl =
            HttpUrl.get(Constants.CHARACTER_URL + character.getAllStarId()[1] + Constants.WEBP)
        //角色图片
        val vh = CharacterListFragment.characterList.findViewHolderForAdapterPosition(
            MainActivity.currentCharaPosition
        ) ?: return
        val v0 = vh.itemView.findViewById<AppCompatImageView>(R.id.character_pic)
        val key = v0.metadata?.memoryCacheKey
        val imageLoader = Coil.imageLoader(requireContext())
        binding.characterPic.load(picUrl) {
            error(R.drawable.error)
            placeholderMemoryCacheKey(key)
            placeholder(R.drawable.load)
            listener(
                onSuccess = { _, _ ->
                    parentFragment?.startPostponedEnterTransition()
                },
                onStart = {
                    requireActivity().supportStartPostponedEnterTransition()
                    val toStart = key == null || imageLoader.memoryCache[key!!] == null
                    if (toStart) {
                        parentFragment?.startPostponedEnterTransition()
                    }
                }
            )
        }
        lifecycleScope.launch {
            delay(resources.getInteger(R.integer.item_anim_fast).toLong())
            canBack = true
        }
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            rightIcon.setOnClickListener {
                isLoved = !isLoved
                setLove(isLoved)
            }
            characterPic.setOnClickListener {
                CharacterPicDialogFragment.getInstance(character).show(parentFragmentManager, "pic")
            }
            //toolbar 展开折叠监听
            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                when {
                    //展开
                    verticalOffset == 0 -> {
                        rightIcon.visibility = View.GONE
                        binding.toolTitle.setTextColor(
                            resources.getColor(
                                R.color.colorAlpha,
                                null
                            )
                        )
                    }
                    abs(verticalOffset) >= appBarLayout!!.totalScrollRange -> {
                        rightIcon.setImageResource(if (isLoved) R.drawable.ic_loved else R.drawable.ic_love)
                        rightIcon.visibility = View.VISIBLE
                        binding.toolTitle.setTextColor(
                            resources.getColor(
                                R.color.colorPrimary,
                                null
                            )
                        )
                    }
                    else -> {
                        if (rightIcon.visibility == View.VISIBLE) rightIcon.visibility = View.GONE
                        binding.toolTitle.setTextColor(
                            resources.getColor(
                                R.color.colorAlpha,
                                null
                            )
                        )
                    }
                }
            })

            //fab点击监听
            fabLoveCbi.setOnClickListener {
                isLoved = !isLoved
                setLove(isLoved)
            }

            //等级点击事件
            binding.promotion.level.setOnClickListener {
                binding.promotion.levelSeekBar.also {
                    if (it.visibility == View.VISIBLE)
                        it.visibility = View.GONE
                    else
                        ObjectAnimatorHelper.alpha(it)
                }
            }
            binding.promotion.levelSeekBar.addOnSliderTouchListener(object :
                Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                    lv = slider.value.toInt()
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    lv = slider.value.toInt()
                    binding.promotion.level.text = slider.value.toInt().toString()
                    loadData()
                }


            })
        }
    }

    //设置收藏
    private fun setLove(isLoved: Boolean) {
        sp.edit {
            putBoolean(
                character.id.toString(),
                isLoved
            )
        }

        val ic = if (isLoved) R.drawable.ic_loved else R.drawable.ic_love
        binding.rightIcon.setImageResource(ic)

        val icFabColor =
            resources.getColor(if (isLoved) R.color.colorPrimary else R.color.alphaPrimary, null)

        binding.name.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (isLoved) R.color.colorPrimary else R.color.text,
                null
            )
        )

        binding.fabLoveCbi.imageTintList = ColorStateList.valueOf(icFabColor)

    }

    private fun setObserve() {
        //角色最大Rank
        viewModel.maxData.observe(viewLifecycleOwner, Observer { r ->
            selRank = r[0]
            selRatity = r[1]
            maxStar = r[1]
            lv = r[2]
            binding.promotion.level.text = lv.toString()
            binding.promotion.levelSeekBar.valueTo = lv.toFloat()
            binding.promotion.levelSeekBar.value = lv.toFloat()
            loadData()

            binding.apply {
                setRank(selRank)
                setRatity(selRatity)
                promotion.rankEquip.rankAdd.setOnClickListener {
                    if (selRank != r[0]) {
                        selRank++
                        if (selRank == r[0]) {
                            it.isEnabled = false
                        } else {
                            promotion.rankEquip.rankReduce.isEnabled = true
                        }
                        setRank(selRank)
                        loadData()
                    }
                }
                promotion.rankEquip.rankReduce.setOnClickListener {
                    if (selRank != Constants.CHARACTER_MIN_RANK) {
                        selRank--
                        if (selRank == 2) {
                            it.isEnabled = false
                        } else {
                            promotion.rankEquip.rankAdd.isEnabled = true
                        }
                        setRank(selRank)
                        loadData()
                    }
                }
            }
        })
        //角色装备
        val equipPics = arrayListOf(
            binding.promotion.rankEquip.pic6,
            binding.promotion.rankEquip.pic5,
            binding.promotion.rankEquip.pic4,
            binding.promotion.rankEquip.pic3,
            binding.promotion.rankEquip.pic2,
            binding.promotion.rankEquip.pic1
        )

        viewModel.equipments.observe(viewLifecycleOwner, Observer {
            it.forEachIndexed { index, equip ->
                equipPics[index].apply {
                    //加载装备图片
                    val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                    this.load(picUrl) {
                        error(R.drawable.error)
                        placeholder(R.drawable.load_mini)
                    }
                    //点击跳转
                    setOnClickListener {
                        if (equip.equipmentId != Constants.UNKNOW_EQUIP_ID) {
                            EquipmentDetailsFragment.getInstance(equip).show(
                                ActivityUtil.instance.currentActivity?.supportFragmentManager!!,
                                "details"
                            )
                        }
                    }
                }
            }
        })
        //角色属性
        viewModel.sumInfo.observe(viewLifecycleOwner, Observer {
            attrAdapter = CharacterAttrAdapter()
            binding.promotion.attrs.adapter = attrAdapter
            attrAdapter.submitList(it.getList())
        })
    }

    private fun loadData() {
        viewModel.getCharacterInfo(character.id, selRank, selRatity, lv)
    }

    //初始化角色基本数据
    private fun setData() {
        binding.apply {
            toolTitle.text =
                if (character.actualName.isEmpty())
                    character.name
                else
                    character.actualName
            catah.text = character.catchCopy
            name.text = character.name
//            character.getNameL().apply {
//                if (this.isNotEmpty()) {
//                    lastName.text = this
//                } else {
//                    lineEx.visibility = View.GONE
//                }
//            }

            three.text = requireActivity().resources.getString(
                R.string.character_detail,
                character.age,
                character.height,
                character.weight,
                character.position
            )
            comment.text = character.getFixedComment()
            if (comment.text.isEmpty()) comment.visibility = View.GONE
            birth.text = requireActivity().resources.getString(
                R.string.birth,
                character.birthMonth,
                character.birthDay
            )
            blood.text =
                requireActivity().resources.getString(R.string.blood, character.bloodType)
            race.text = character.race
            guide.text = character.guild
            favorite.text = character.favorite
            cv.text = character.voice
            self.text = character.getSelf()
            positionType.background =
                ResourcesCompat.getDrawable(
                    resources,
                    character.getPositionIcon(),
                    null
                )
            loveSelfText.text = character.getLoveSelfText()
            //头像
            val iconUrl = Constants.UNIT_ICON_URL + character.getStarId(3) + Constants.WEBP
            promotion.icon.load(iconUrl) {
                error(R.drawable.unknow)
                placeholder(R.drawable.load_mini)
            }
            //TODO 专武
//            promotion.uniqueEquip.detail.apply {
//                //TODO 专武信息获取
//                val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
//                itemPic.load(picUrl) {
//                    error(R.drawable.error)
//                }
//                //描述
//                desc.text = equip.getDesc()
//                //属性词条
//                val adapter = EquipmentAttrAdapter()
//                attrs.adapter = adapter
//                adapter.submitList(equip.getAttrs())
//            }
        }
    }

    //设置rank
    private fun setRank(num: Int) {
        binding.promotion.rankEquip.apply {
            rank.text = num.toString()
            rank.setTextColor(getRankColor(num))
            rankTitle.setTextColor(getRankColor(num))
        }
    }

    //设置星级
    private fun setRatity(num: Int) {
        StarUtil.show(
            binding.root.context,
            binding.promotion.starts,
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

    //rank 颜色
    private fun getRankColor(rank: Int): Int {
        val color = when (rank) {
            in 2..3 -> R.color.color_rank_2_3
            in 4..6 -> R.color.color_rank_4_6
            in 7..10 -> R.color.color_rank_7_10
            in 11..99 -> R.color.color_rank_11
            else -> {
                R.color.color_rank_2_3
            }
        }
        return ResourcesCompat.getColor(resources, color, null)
    }
}
