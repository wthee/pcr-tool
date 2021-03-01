package cn.wthee.pcrtool.ui.character.attr

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.adapter.RankAdapter
import cn.wthee.pcrtool.databinding.FragmentRankSelectDialogBinding
import cn.wthee.pcrtool.utils.Constants


/**
 * Rank 选择页面
 *
 * 页面布局 [FragmentRankSelectDialogBinding]
 *
 * ViewModels []
 */
class RankSelectDialogFragment(
    private val preFragment: Fragment,
    private val requestCode: Int
) : DialogFragment() {

    private lateinit var binding: FragmentRankSelectDialogBinding
    private var selectRank = 1
    private var startRank = 1
    private var endRank = 1
    private lateinit var adapter: RankAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            selectRank = getInt(Constants.SELECT_RANK)
            startRank = getInt(Constants.START_RANK)
            endRank = getInt(Constants.END_RANK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankSelectDialogBinding.inflate(layoutInflater, container, false)
        val ranks = arrayListOf<Int>()
        for (i in endRank downTo startRank) {
            ranks.add(i)
        }
        adapter = RankAdapter(this)
        binding.listRank.adapter = adapter
        adapter.submitList(ranks)
        adapter.setRank(selectRank)

        setTargetFragment(preFragment, requestCode)

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        //获取已选择的数据，并返回
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt(Constants.SELECT_RANK, adapter.getRank())
        intent.putExtras(bundle)
        targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
//            val width = ScreenUtil.getWidth() - 42.dp
//            setLayout(width, (width / 0.618f).toInt())
//            setGravity(Gravity.BOTTOM)
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            setWindowAnimations(R.style.DialogAnimation)
//            val params = attributes
//            params.y = 15.dp
//            attributes = params
        }
    }

    fun getInstance(selectRank: Int, startRank: Int, endRank: Int) =
        this.apply {
            arguments = Bundle().apply {
                putInt(Constants.SELECT_RANK, selectRank)
                putInt(Constants.START_RANK, startRank)
                putInt(Constants.END_RANK, endRank)
            }
        }
}