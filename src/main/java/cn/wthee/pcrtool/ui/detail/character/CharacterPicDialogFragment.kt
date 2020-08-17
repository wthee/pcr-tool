package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.PagerSnapHelper
import cn.wthee.pcrtool.adapters.CharacterCardBgAdapter
import cn.wthee.pcrtool.data.model.entity.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.FragmentCharacterPicListBinding

class CharacterPicDialogFragment : DialogFragment() {

    companion object {
        fun getInstance(characterInfo: CharacterBasicInfo): CharacterPicDialogFragment {
            val fragment = CharacterPicDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable("character", characterInfo)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentCharacterPicListBinding
    private lateinit var character: CharacterBasicInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = requireArguments().getSerializable("character") as CharacterBasicInfo
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
        adapter.submitList(character.getAllUrl())
        binding.pics.scrollToPosition(2)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window

        val params = window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes = params
    }
}