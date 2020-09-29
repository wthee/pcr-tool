package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import cn.wthee.pcrtool.adapters.CharacterCardBgAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPicListBinding


class CharacterPicDialogFragment : DialogFragment() {

    companion object {
        fun getInstance(urls: ArrayList<String>): CharacterPicDialogFragment {
            val fragment = CharacterPicDialogFragment()
            val bundle = Bundle()
            bundle.putStringArrayList("urls", urls)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentCharacterPicListBinding
    private lateinit var urls: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urls = requireArguments().getStringArrayList("urls") as ArrayList<String>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterPicListBinding.inflate(inflater, container, false)
        //初始化列表
        val adapter = CharacterCardBgAdapter()
        PagerSnapHelper().attachToRecyclerView(binding.pics)
        binding.pics.adapter = adapter
        adapter.submitList(urls)
        //返回
        binding.backCbi.setOnClickListener {
            dismiss()
        }
        //指示器
        binding.pics.setOnScrollChangeListener { _, _, _, _, _ ->
            val manager = binding.pics.layoutManager as LinearLayoutManager
            val index = manager.findFirstCompletelyVisibleItemPosition() + 1
            if (index != 0) binding.picIndex.text = "$index / ${urls.size}"
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

}