package cn.wthee.pcrtool.adapter.load

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.LayoutLoadingDialogBinding
import com.google.android.material.card.MaterialCardView

/**
 * 列表加载状态适配器
 */
class LoaderStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoaderStateAdapter.LoaderViewHolder>() {

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        return LoaderViewHolder(
            LayoutLoadingDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root, retry
        )
    }

    class LoaderViewHolder(view: View, retry: () -> Unit) : RecyclerView.ViewHolder(view) {

        private val loading: MaterialCardView = view.findViewById(R.id.loading_dialog)

        init {
            view.setOnClickListener {
                retry()
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Loading) {
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }
        }
    }
}