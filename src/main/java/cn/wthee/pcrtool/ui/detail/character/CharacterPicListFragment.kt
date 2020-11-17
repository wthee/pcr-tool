package cn.wthee.pcrtool.ui.detail.character

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterPicViewPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPicListBinding
import cn.wthee.pcrtool.utils.ImageDownloadUtil
import cn.wthee.pcrtool.utils.ToastUtil
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.launch


class CharacterPicListFragment : Fragment() {

    companion object {
        var hasLoaded = arrayListOf(false, false, false, false)
    }

    private lateinit var binding: FragmentCharacterPicListBinding
    private lateinit var urls: ArrayList<String>
    private var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urls = requireArguments().getStringArrayList("urls") as ArrayList<String>
        //过渡
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = resources.getInteger(R.integer.fragment_anim).toLong()
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterPicListBinding.inflate(inflater, container, false)
        binding.apply {
            //初始化列表
            val endlessScrollAdapter = CharacterPicViewPagerAdapter(childFragmentManager, lifecycle)
            pics.adapter = endlessScrollAdapter
            endlessScrollAdapter.apply {
                updateList(urls)
                pics.setCurrentItem(this.firstElementPosition, false)
            }
            //指示器
            picIndex.text = getString(R.string.pic_index, index, urls.size)
            pics.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    //index显示

                    index =
                        (urls.size + (position - endlessScrollAdapter.firstElementPosition) % urls.size) % urls.size + 1
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    picIndex.text = getString(R.string.pic_index, index, urls.size)
                }
            })
            //下载
            fabDownload.setOnClickListener {
                if (index != 0) {
                    if (hasLoaded[index - 1]) {
                        try {
                            val url = urls[index - 1]
                            val pre = url.substring(url.indexOf("card/") + 5, url.lastIndexOf('/'))
                            val num = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'))
                            val name = "${pre}_${num}.png"
                            //保存图片
                            lifecycleScope.launch {
                                val request = ImageRequest.Builder(requireContext())
                                    .data(url)
                                    .build()
                                val bitmap =
                                    (requireContext().imageLoader.execute(request).drawable as BitmapDrawable).bitmap
                                ImageDownloadUtil(requireActivity()).save(bitmap, name)
                            }

                        } catch (e: Exception) {
                            Log.e("save", e.message ?: "")
                            ToastUtil.short("未保存成功，请重试~")
                        }
                    } else {
                        ToastUtil.short("请等待图片加载完成~")
                    }
                }
            }
            //返回
            fabBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return binding.root
    }


}