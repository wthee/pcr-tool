package cn.wthee.pcrtool.ui.tool.clan

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CallBack
import cn.wthee.pcrtool.adapter.NumberSelectAdapter
import cn.wthee.pcrtool.data.enums.NumberSelectType
import cn.wthee.pcrtool.databinding.FragmentRankSelectDialogBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.dp


/**
 * Rank 选择页面
 *
 * 页面布局 [FragmentRankSelectDialogBinding]
 *
 * ViewModels []
 */
class ClanSectionSelectDialogFragment(
    private val preFragment: Fragment,
    private val requestCode: Int
) : DialogFragment() {


    private lateinit var binding: FragmentRankSelectDialogBinding
    private var maxSection = 3
    private var selSection = 3
    private lateinit var adapter: NumberSelectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            maxSection = getInt(Constants.CLAN_MAX_SECTION)
            selSection = getInt(Constants.CLAN_SELECT_SECTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankSelectDialogBinding.inflate(layoutInflater, container, false)
        val ranks = arrayListOf<Int>()
        for (i in 1..maxSection) {
            ranks.add(i)
        }
        adapter = NumberSelectAdapter(NumberSelectType.SECTION, object : CallBack {
            override fun todo(data: Any?) {
                this@ClanSectionSelectDialogFragment.dismiss()
            }
        })
        binding.listRank.adapter = adapter
        adapter.submitList(ranks)
        adapter.setSelect(selSection)

        setTargetFragment(preFragment, requestCode)

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        //获取已选择的数据，并返回
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt(Constants.CLAN_SELECT_SECTION, adapter.getSelect())
        intent.putExtras(bundle)
        targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWindowAnimations(R.style.DialogAnimation)
            val params = attributes
            params.y = 15.dp
            attributes = params
        }
    }

    fun getInstance(maxSection: Int, selSection: Int) =
        this.apply {
            arguments = Bundle().apply {
                putInt(Constants.CLAN_MAX_SECTION, maxSection)
                putInt(Constants.CLAN_SELECT_SECTION, selSection)
            }
        }
}