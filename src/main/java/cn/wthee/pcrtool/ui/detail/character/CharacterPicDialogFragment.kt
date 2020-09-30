package cn.wthee.pcrtool.ui.detail.character

import android.content.DialogInterface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterPicAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPicListBinding
import cn.wthee.pcrtool.utils.ImageDownloadUtil
import cn.wthee.pcrtool.utils.ToastUtil
import com.github.chrisbanes.photoview.PhotoView


class CharacterPicDialogFragment : DialogFragment() {

    companion object {
        var hasLoaded = arrayListOf<Boolean>()

        fun getInstance(urls: ArrayList<String>): CharacterPicDialogFragment {
            val fragment = CharacterPicDialogFragment()
            val bundle = Bundle()
            bundle.putStringArrayList("urls", urls)
            fragment.arguments = bundle
            hasLoaded = arrayListOf(false, false, false, false)
            return fragment
        }

    }

    private lateinit var binding: FragmentCharacterPicListBinding
    private lateinit var urls: ArrayList<String>
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.fabMain.hide()
        urls = requireArguments().getStringArrayList("urls") as ArrayList<String>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterPicListBinding.inflate(inflater, container, false)
        binding.apply {
            //初始化列表
            val adapter = CharacterPicAdapter()
            PagerSnapHelper().attachToRecyclerView(binding.pics)
            pics.adapter = adapter
            adapter.submitList(urls)
            //指示器
            pics.setOnScrollChangeListener { _, _, _, _, _ ->
                val manager = pics.layoutManager as LinearLayoutManager
                index = manager.findFirstCompletelyVisibleItemPosition() + 1
                if (index != 0) picIndex.text = "$index / ${urls.size}"
            }
            //下载
            download.setOnClickListener {
                if (index != 0) {
                    if (hasLoaded[index - 1]) {
                        try {
                            val url = urls[index - 1]
                            val pre = url.substring(url.indexOf("card/") + 5, url.lastIndexOf('/'))
                            val num = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'))
                            val name = "${pre}_${num}.png"
                            val vh = pics.findViewHolderForAdapterPosition(index - 1)
                            val image = vh?.itemView?.findViewById<PhotoView>(R.id.character_pic)
                            val bitmap = (image!!.drawable as BitmapDrawable).bitmap
                            ImageDownloadUtil(requireContext()).save(bitmap, name)
                        } catch (e: Exception) {
                            Log.e("save", e.message ?: "")
                            ToastUtil.short("保存出错，请重试~")
                        }
                    } else {
                        ToastUtil.short("请等待图片加加载~")
                    }
                }
            }
            //返回
            picsBack.setOnClickListener {
                dismiss()
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val params = window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        params?.horizontalMargin = 0f
        params?.verticalMargin = 0f
        window?.attributes = params
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.fabMain.show()
    }
}