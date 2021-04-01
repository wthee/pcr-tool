package cn.wthee.pcrtool.ui.character

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterPicAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPicListBinding
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.launch

/**
 * 角色图片展示页面弹窗
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterPicListBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class CharacterPicListFragment : Fragment() {

    companion object {

        fun getInstance(uid: Int) = CharacterPicListFragment().apply {
            arguments = Bundle().apply {
                putInt(Constants.UID, uid)
            }
        }
    }

    private lateinit var binding: FragmentCharacterPicListBinding
    private lateinit var adapter: CharacterPicAdapter
    private var uid = -1
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FabHelper.addBackFab(2)
        requireArguments().apply {
            uid = getInt(Constants.UID)
        }
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 500L
            setAllContainerColors(ResourcesUtil.getColor(R.color.colorWhite))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterPicListBinding.inflate(inflater, container, false)
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }

        binding.apply {
            //初始化列表
            lifecycleScope.launch {
                adapter = CharacterPicAdapter(this@CharacterPicListFragment)
                pics.adapter = adapter
                val picData = CharacterIdUtil.getAllPicUrl(
                    uid,
                    sharedCharacterViewModel.getR6Ids().contains(uid)
                )
                adapter.submitList(picData)
            }
        }

        binding.downloadTip.postDelayed({
            binding.downloadTip.visibility = View.INVISIBLE
        }, 1500L)
        return binding.root
    }
}