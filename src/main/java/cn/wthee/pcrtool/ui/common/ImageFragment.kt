package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentImageBinding
import coil.load


class ImageFragment : Fragment() {

    private val URL = "url"
    private var url: String? = null
    private lateinit var binding: FragmentImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        binding.apply {
            image.transitionName = url
            image.load(url) {
                placeholder(R.drawable.load)
                error(R.drawable.error)
                listener(
                    onStart = {
                        parentFragment?.startPostponedEnterTransition()
                    }
                )
            }
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(url: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, url)
                }
            }
    }
}