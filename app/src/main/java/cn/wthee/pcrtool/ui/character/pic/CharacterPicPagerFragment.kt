package cn.wthee.pcrtool.ui.character.pic

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.adapter.viewpager.CharacterPicPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPicPagerBinding
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.material.transition.MaterialContainerTransform
import com.permissionx.guolindev.PermissionX
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色图片展示页面弹窗
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterPicPagerBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class CharacterPicPagerFragment : Fragment() {

    companion object {
        val loaded = arrayListOf(false, false, false)

        fun getInstance(uid: Int) = CharacterPicPagerFragment().apply {
            arguments = Bundle().apply {
                putInt(Constants.UID, uid)
            }
        }
    }

    private lateinit var binding: FragmentCharacterPicPagerBinding
    private lateinit var adapter: CharacterPicPagerAdapter
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
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterPicPagerBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        binding.apply {
            //初始化列表
            lifecycleScope.launch {
                val picData = CharacterIdUtil.getAllPicUrl(
                    uid,
                    sharedCharacterViewModel.getR6Ids().contains(uid)
                )
                adapter = CharacterPicPagerAdapter(childFragmentManager, lifecycle, picData)
                pics.adapter = adapter
                pics.offscreenPageLimit = 4
                ViewPagerHelper(pics, requireContext()).adjustViewPager()

                var index = 0
                pics.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        index = position
                    }
                })
                //设置点击事件
                fabDownload.setOnClickListener {
                    val url = picData[index]
                    //权限申请
                    PermissionX.init(parentFragment).permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ).request { allGranted, _, _ ->
                        if (allGranted) {
                            //开始下载
                            if (loaded[index]) {
                                ToastUtil.short("正在保存，请稍后~")
                                try {
                                    val pre =
                                        url.substring(
                                            url.indexOf("card/") + 5,
                                            url.lastIndexOf('/')
                                        )
                                    val num =
                                        url.substring(
                                            url.lastIndexOf('/') + 1,
                                            url.lastIndexOf('.')
                                        )
                                    val name = "${pre}_${num}.png"
                                    //保存图片
                                    MainScope().launch {
                                        val request =
                                            ImageRequest.Builder(requireContext())
                                                .data(url)
                                                .build()
                                        val bitmap =
                                            (requireContext().imageLoader.execute(
                                                request
                                            ).drawable as BitmapDrawable).bitmap
                                        ImageDownloadHelper(requireActivity()).save(
                                            bitmap,
                                            name
                                        )
                                    }
                                } catch (e: Exception) {
                                    MainScope().launch {
                                        UMCrash.generateCustomLog(
                                            e,
                                            Constants.EXCEPTION_DOWNLOAD_PIC + "url:$url"
                                        )
                                    }
                                    ToastUtil.short("图片保存失败~")
                                }
                            } else {
                                ToastUtil.short("请等待图片加载完成~")
                            }
                        } else {
                            ToastUtil.short("无法保存~请允许相关权限")
                        }
                    }
                }
            }

        }

        return binding.root
    }
}