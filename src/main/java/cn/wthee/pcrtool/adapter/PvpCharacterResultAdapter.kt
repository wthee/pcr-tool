package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.data.network.model.PvpData
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.ItemPvpResultBinding
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.dp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


class PvpCharacterResultAdapter(
    private val isFloat: Boolean
) :
    ListAdapter<PvpData, PvpCharacterResultAdapter.ViewHolder>(PvpResultDiffCallback()) {

    val dao = AppPvpDatabase.getInstance().getPvpDao()
    val region = DatabaseUpdater.getRegion()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPvpResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemPvpResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(data: PvpData) {
            binding.apply {
                //调整布局
                val listParams = atkCharacters.layoutParams as ConstraintLayout.LayoutParams
                val starParams = star.layoutParams as ConstraintLayout.LayoutParams
                if (isFloat) {
                    listParams.matchConstraintPercentWidth = 1f
                    listParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    starParams.startToEnd = down.id
                    starParams.topToBottom = atkCharacters.id
                    starParams.width = 18.dp
                } else {
                    listParams.matchConstraintPercentWidth = 0.8f
                    starParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    starParams.topToTop = atkCharacters.id
                    starParams.bottomToBottom = atkCharacters.id
                    starParams.width = 28.dp
                }
                star.layoutParams = starParams
                atkCharacters.layoutParams = listParams

                //初始化颜色
                MainScope().launch {
                    if (dao.getLiked(data.atk, data.def, region, 0) != null) {
                        star.imageTintList =
                            ColorStateList.valueOf(ResourcesUtil.getColor(R.color.colorPrimary))
                    } else {
                        star.imageTintList =
                            ColorStateList.valueOf(ResourcesUtil.getColor(R.color.textGray))
                    }
                }
                //进攻角色列表
                val adapter = PvpCharacterResultItemAdapter()
                atkCharacters.adapter = adapter
                adapter.submitList(data.getAtkIdList())
                //顶/踩信息
                up.text = "${data.up}"
                down.text = "${data.down}"
                val rateNum = if (data.up == 0) 0 else {
                    round(data.up * 1.0 / (data.up + data.down) * 100).toInt()
                }
                rate.text = "$rateNum %"
                //收藏监听
                star.setOnClickListener {
                    MainScope().launch {
                        if (dao.getLiked(data.atk, data.def, region, 0) != null) {
                            //已收藏，取消收藏
                            dao.delete(data.atk, data.def, region)
                            star.imageTintList =
                                ColorStateList.valueOf(ResourcesUtil.getColor(R.color.textGray))
                        } else {
                            //未收藏，添加收藏
                            val simpleDateFormat =
                                SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS") // HH:mm:ss
                            val date = Date(System.currentTimeMillis())
                            dao.insert(
                                PvpLikedData(
                                    data.id,
                                    data.atk,
                                    data.def,
                                    simpleDateFormat.format(date),
                                    region
                                )
                            )
                            star.imageTintList =
                                ColorStateList.valueOf(ResourcesUtil.getColor(R.color.colorPrimary))

                        }
                    }
                }
            }

        }
    }
}

class PvpResultDiffCallback : DiffUtil.ItemCallback<PvpData>() {

    override fun areItemsTheSame(
        oldItem: PvpData,
        newItem: PvpData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: PvpData,
        newItem: PvpData
    ): Boolean {
        return oldItem.id == newItem.id
    }
}
