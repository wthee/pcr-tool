package cn.wthee.pcrtool.adapter

import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.CharacterPicData
import cn.wthee.pcrtool.databinding.ItemPicBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageDownloadHelper
import cn.wthee.pcrtool.utils.ToastUtil
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.permissionx.guolindev.PermissionX
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色详情图片列表适配器
 *
 * 列表项布局 [ItemPicBinding]
 *
 * 列表项数据 [CharacterPicData] 图片链接
 */
class CharacterPicAdapter(private val parentFragment: Fragment) :
    ListAdapter<CharacterPicData, CharacterPicAdapter.ViewHolder>(CharacterImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemPicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CharacterPicData) {
            //设置数据
            binding.apply {
                var load = false
                pic.transitionName = data.picUrl
                //图片类型
                picType.text = data.picType
                //加载图片
                pic.load(data.picUrl) {
                    error(R.drawable.error)
                    placeholder(R.drawable.load)
                    listener(
                        onStart = {
                            parentFragment.startPostponedEnterTransition()
                        },
                        onSuccess = { _, _ ->
                            load = true
                        }
                    )
                }
                root.setOnLongClickListener {
                    //权限申请
                    PermissionX.init(parentFragment).permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ).request { allGranted, _, _ ->
                        if (allGranted) {
                            //开始下载
                            if (load) {
                                ToastUtil.short("正在保存，请稍后~")
                                val url = data.picUrl
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
                                            ImageRequest.Builder(parentFragment.requireContext())
                                                .data(url)
                                                .build()
                                        val bitmap =
                                            (parentFragment.requireContext().imageLoader.execute(
                                                request
                                            ).drawable as BitmapDrawable).bitmap
                                        ImageDownloadHelper(parentFragment.requireActivity()).save(
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
                    return@setOnLongClickListener true
                }
            }
        }
    }
}

class CharacterImageDiffCallback : DiffUtil.ItemCallback<CharacterPicData>() {

    override fun areItemsTheSame(
        oldItem: CharacterPicData,
        newItem: CharacterPicData
    ): Boolean {
        return oldItem.picType == newItem.picType
    }

    override fun areContentsTheSame(
        oldItem: CharacterPicData,
        newItem: CharacterPicData
    ): Boolean {
        return oldItem == newItem
    }
}
