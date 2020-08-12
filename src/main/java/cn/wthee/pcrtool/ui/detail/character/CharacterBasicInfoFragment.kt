package cn.wthee.pcrtool.ui.detail.character

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MainActivity.Companion.canBack
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.MainActivity.Companion.spFirstClick
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterAttrAdapter
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.data.model.getList
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.databinding.LayoutSearchBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.utils.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterBasicInfoBinding.inflate(inflater, container, false)
        binding.content.love.visibility = View.GONE
        //设置共享元素
        binding.characterPic.transitionName = "img_${character.id}"
        binding.content.info.transitionName = "content_${character.id}"
        //开始动画
        ObjectAnimatorHelper.alpha(binding.fabLoveCbi)
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
        }, binding.basicInfo, binding.promotion.root)

        //列表适配器
        attrAdapter = CharacterAttrAdapter()
        binding.promotion.attrs.adapter = attrAdapter
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
        GlideUtil.loadWithListener(
            Constants.CHARACTER_URL + character.getAllStarId()[1] + Constants.WEBP,
            binding.characterPic,
            R.drawable.error,
            parentFragment,
            object : OnLoadListener {
                override fun onSuccess() {
                    spFirstClick.edit {
                        putBoolean("first_click_${character.id}", false)
                        lifecycleScope.launch {
                            try {
                                delay(resources.getInteger(R.integer.item_anim_fast).toLong())
                                canBack = true
                            } catch (e: Exception) {

                            }
                        }

                    }
                }
            }
        )

        val first = spFirstClick.getBoolean("first_click_${character.id}", true)
        if (first) {
            parentFragment?.startPostponedEnterTransition()
            lifecycleScope.launch {
                delay(resources.getInteger(R.integer.item_anim_fast).toLong())
                canBack = true
            }
        }
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_love -> {
                        isLoved = !isLoved
                        setLove(isLoved)
                        true
                    }
                    else -> false
                }
            }
            characterPic.setOnClickListener {
                CharacterPicDialogFragment.getInstance(character).show(parentFragmentManager, "pic")
            }
            //toolbar 展开折叠监听
            val menu = toolbar.menu
            val shareMenu = menu.getItem(0)
            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                when {
                    //展开
                    verticalOffset == 0 -> {
                        shareMenu.isVisible = false
                        binding.layoutToolbar.setCollapsedTitleTextColor(
                            resources.getColor(
                                R.color.colorAlpha,
                                null
                            )
                        )

                    }
                    abs(verticalOffset) >= appBarLayout!!.totalScrollRange - 5 -> {
                        shareMenu.setIcon(if (isLoved) R.drawable.ic_loved else R.drawable.ic_love)
                        shareMenu.isVisible = true
                        binding.layoutToolbar.setCollapsedTitleTextColor(
                            resources.getColor(
                                R.color.colorPrimary,
                                null
                            )
                        )
                    }
                    else -> {
                        if (shareMenu.icon.alpha != 0) shareMenu.isVisible = false
                        binding.layoutToolbar.setCollapsedTitleTextColor(
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
            binding.promotion.icon.setOnClickListener {
                binding.promotion.level.callOnClick()
            }
            binding.promotion.level.setOnClickListener {
                val layout = LayoutSearchBinding.inflate(layoutInflater)
                DialogUtil.create(requireContext(), layout.root).show()
                //搜索框
                val searchView = layout.searchInput
                searchView.onActionViewExpanded()
                searchView.isSubmitButtonEnabled = true
                searchView.queryHint = "请输入等级"
                searchView.inputType = InputType.TYPE_CLASS_NUMBER
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        binding.promotion.level.text = query
                        lv = query?.toInt() ?: lv
                        loadData()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText?.length!! > 3) {
                            searchView.setQuery(newText.substring(0, 3), false)
                        }
                        return false
                    }
                })
            }
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
        val menu = binding.toolbar.menu
        val shareMenu = menu.getItem(0)
        shareMenu.setIcon(ic)

        val icFabColor = if (isLoved) resources.getColor(
            R.color.colorPrimary,
            null
        ) else resources.getColor(R.color.alphaPrimary, null)
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
            loadData()

            binding.apply {
                setRank(selRank)
                setRatity(selRatity)
                promotion.rankEquip.rankAdd.setOnClickListener {
                    if (selRank != r[0]) {
                        selRank++
                        setRank(selRank)
                        loadData()
                    }
                }
                promotion.rankEquip.rankReduce.setOnClickListener {
                    if (selRank != Constants.CHARACTER_MIN_RANK) {
                        selRank--
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
                    //加载图片
                    //加载装备图片
                    val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                    GlideUtil.load(picUrl, this, R.drawable.error, null)
                    //点击跳转
                    setOnClickListener {
                        if (equip.equipmentId != Constants.UNKNOW_EQUIP_ID) {
                            EquipmentDetailsFragment.getInstance(equip, true).show(
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
            attrAdapter.submitList(it.getList())
        })
    }

    private fun loadData() {
        viewModel.getCharacterInfo(character.id, selRank, selRatity, lv)
    }

    //初始化角色基本数据
    private fun setData() {
        binding.apply {
            toolbar.title =
                if (character.actualName.isEmpty())
                    character.name
                else
                    character.actualName
            content.catah.text = character.catchCopy
            content.name.text = character.name
//            character.getNameL().apply {
//                if (this.isNotEmpty()) {
//                    lastName.text = this
//                } else {
//                    lineEx.visibility = View.GONE
//                }
//            }

            content.three.text = requireActivity().resources.getString(
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
            content.positionType.background =
                ResourcesCompat.getDrawable(
                    resources,
                    character.getPositionIcon(),
                    null
                )
            loveSelfText.text = character.getLoveSelfText()

            val iconUrl = Constants.UNIT_ICON_URL + character.getFixedId() + Constants.WEBP
            GlideUtil.load(iconUrl,promotion.icon,R.drawable.unknow,null)
        }
    }

    //设置rank
    private fun setRank(num: Int){
        binding.promotion.rankEquip.apply {
            rank.text = num.toString()
            rank.setTextColor(getRankColor(num))
            rankTitle.setTextColor(getRankColor(num))
        }
    }

    //设置星级
    private fun setRatity(num: Int){
        StarUtil.show(binding.root.context, binding.promotion.starts, num, maxStar, 40, object : StarUtil.OnSelect{
            override fun select(index: Int) {
                selRatity = index + 1
                loadData()
            }
        })
    }

    //rank 颜色
    private fun getRankColor(rank: Int): Int {
        val color = when(rank){
            in 2 .. 3 -> R.color.color_rank_2_3
            in 4 .. 6 -> R.color.color_rank_4_6
            in 7 .. 10 -> R.color.color_rank_7_10
            in 11 .. 99 -> R.color.color_rank_11
            else ->{
                R.color.color_rank_2_3
            }
        }
        return ResourcesCompat.getColor(resources, color, null)
    }
}
