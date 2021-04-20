package cn.wthee.pcrtool.ui.character.pic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentCharacterPicBinding
import cn.wthee.pcrtool.databinding.FragmentCharacterPicPagerBinding
import cn.wthee.pcrtool.utils.*
import coil.load
import dagger.hilt.android.AndroidEntryPoint

/**
 * 角色图片展示页面弹窗
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterPicPagerBinding]
 *
 * ViewModels []
 */
@AndroidEntryPoint
class CharacterPicFragment : Fragment() {
    private val PIC_URL = "pic_url"
    private val PIC_INDEX = "pic_index"

    companion object {

        fun getInstance(index: Int, url: String) =
            CharacterPicFragment().apply {
                arguments = Bundle().apply {
                    putInt(PIC_INDEX, index)
                    putString(PIC_URL, url)
                }
            }
    }

    private lateinit var binding: FragmentCharacterPicBinding
    private lateinit var url: String
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FabHelper.addBackFab(2)
        requireArguments().apply {
            url = getString(PIC_URL) ?: ""
            index = getInt(PIC_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterPicBinding.inflate(inflater, container, false)
        binding.apply {
            pic.transitionName = url
            pic.load(url) {
                error(R.drawable.error)
                placeholder(R.drawable.load)
                listener(
                    onStart = {
                        parentFragment?.startPostponedEnterTransition()
                    },
                    onSuccess = { _, _ ->
                        CharacterPicPagerFragment.loaded[index] = true
                    }
                )
            }
        }

        return binding.root
    }
}