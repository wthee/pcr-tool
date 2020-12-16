package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentImageBinding
import cn.wthee.pcrtool.ui.detail.character.basic.CharacterPicListFragment
import coil.load

/**
 * 图片展示页面
 */
class ImageFragment : Fragment() {

    private val URL = "url"
    private val POSITION = "position"
    private var position = 0
    private var url: String? = null
    private lateinit var binding: FragmentImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(POSITION)
            url = it.getString(URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        binding.image.apply {
            transitionName = url
            load(url) {
                placeholder(R.drawable.load)
                error(R.drawable.error)
                listener(
                    onStart = {
                        parentFragment?.startPostponedEnterTransition()
                    },
                    onSuccess = { _, _ ->
                        CharacterPicListFragment.hasLoaded[position] = true
                    }
                )
            }
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int, url: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putInt(POSITION, position)
                    putString(URL, url)
                }
            }
    }
}