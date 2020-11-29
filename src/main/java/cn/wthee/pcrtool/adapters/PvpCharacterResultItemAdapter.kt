package cn.wthee.pcrtool.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.model.Atk
import cn.wthee.pcrtool.databinding.ItemPvpResultIconBinding
import cn.wthee.pcrtool.ui.main.CharacterListFragment.Companion.r6Ids
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import coil.Coil
import coil.request.ImageRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class PvpCharacterResultItemAdapter(
    private val activity: Activity
) :
    ListAdapter<Atk, PvpCharacterResultItemAdapter.ViewHolder>(PvpResultItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPvpResultIconBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemPvpResultIconBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Atk) {
            //设置数据
            binding.apply {
                var id = data.id
                id += if (r6Ids.contains(id)) 60 else 30
                val picUrl = UNIT_ICON_URL + id + WEBP
                val coil = Coil.imageLoader(activity.applicationContext)
                val request = ImageRequest.Builder(activity.applicationContext)
                    .data(picUrl)
                    .build()
                MainScope().launch {
                    icon.setImageDrawable(coil.execute(request).drawable)
                }
            }
        }
    }
}

class PvpResultItemDiffCallback : DiffUtil.ItemCallback<Atk>() {

    override fun areItemsTheSame(
        oldItem: Atk,
        newItem: Atk
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Atk,
        newItem: Atk
    ): Boolean {
        return oldItem == newItem
    }
}
