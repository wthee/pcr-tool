package cn.wthee.pcrtool.ui.detail.character

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterCardBgAdapter
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.MainScope
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
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var ic = R.drawable.ic_love
    private var icFab = R.drawable.ic_love_no_bg
    private var icFabColor = MyApplication.getContext().resources.getColor(R.color.white, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
        //设置共享元素
        binding.icon1.transitionName = "img_${character.id}"
        //加载图片
        loadImages()
        //点击事件
        setListener()
        //初始化数据
        setData()
        setHasOptionsMenu(true)
        return binding.root
    }

    //加载图片
    private fun loadImages() {
        //加载角色ICON
        val picUrl =
            Constants.CHARACTER_ICON_URL + character.getAllStarId()[0] + Constants.WEPB
        GlideUtil.load(picUrl, binding.icon1, R.drawable.unknow, parentFragment)
        //加载角色现实图片
        GlideUtil.load(
            Constants.Reality_CHARACTER_URL + character.getFixedId() + Constants.WEPB,
            binding.characterPic,
            R.drawable.error,
            null
        )
        parentFragment?.startPostponedEnterTransition()
        //加载角色卡面列表
        MainScope().launch {
            delay(resources.getInteger(R.integer.delay).toLong())
            val adapter = CharacterCardBgAdapter()
            binding.recycler.adapter = adapter
            linearLayoutManager = LinearLayoutManager(MyApplication.getContext())
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.recycler.layoutManager = linearLayoutManager
            adapter.submitList(character.getAllStarId())
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
                        sp.edit {
                            putBoolean(character.id.toString(),
                                isLoved
                            )
                        }
                        setLove(isLoved)
                        true
                    }
                    else -> false
                }
            }
            //toolbar 展开折叠监听
            val menu = toolbar.menu
            val shareMenu = menu.getItem(0)
            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                when {
                    //展开
                    verticalOffset == 0 -> shareMenu.icon.alpha = 0
                    abs(verticalOffset) >= appBarLayout!!.totalScrollRange -> {
                        shareMenu.setIcon(ic)
                        shareMenu.icon.alpha = 255
                    }
                    else -> {
                        if (shareMenu.icon.alpha != 0) shareMenu.icon.alpha = 0
                    }
                }
            })
            //fab点击监听
            fab.setOnClickListener {
                isLoved = !isLoved
                sp.edit {
                    putBoolean(character.id.toString(),
                        isLoved
                    )
                }
                setLove(isLoved)
            }
            setLove(isLoved)
        }
    }

    private fun setLove(isLoved: Boolean) {
        val menu = binding.toolbar.menu
        val shareMenu = menu.getItem(0)
        ic = if (isLoved) R.drawable.ic_loved else R.drawable.ic_love
        icFab = if (isLoved) R.drawable.ic_loved_no_bg else R.drawable.ic_love_no_bg
        icFabColor = if (isLoved) resources.getColor(
            R.color.blue,
            null
        ) else resources.getColor(R.color.alphaPrimary, null)
        binding.fab.setImageDrawable(resources.getDrawable(icFab, null))
        binding.fab.imageTintList = ColorStateList.valueOf(icFabColor)
        shareMenu.setIcon(ic)
    }

    //初始化角色基本数据
    private fun setData() {
        binding.apply {
            layoutToolbar.title =
                if (character.actualName.isEmpty())
                    character.name
                else
                    character.actualName
            catah.text = character.catchCopy
            name.text = character.getNameF()
            character.getNameL().apply {
                if (this.isNotEmpty()) {
                    lastName.text = this
                } else {
                    lineEx.visibility = View.GONE
                }
            }
            age.text = character.age
            height.text =
                requireActivity().resources.getString(R.string.height, character.height)
            weight.text =
                requireActivity().resources.getString(R.string.weight, character.weight)
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
            position.text = character.position.toString()
            positionType.background =
                resources.getDrawable(
                    character.getPositionIcon(),
                    null
                )
            loveSelfText.text = character.getLoveSelfText()
        }

    }
}
