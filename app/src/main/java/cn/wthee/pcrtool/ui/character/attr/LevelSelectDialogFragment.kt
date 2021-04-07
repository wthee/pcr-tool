package cn.wthee.pcrtool.ui.character.attr

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
import cn.wthee.pcrtool.databinding.LayoutSliderBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp
import com.google.android.material.slider.Slider


/**
 * 等级选择页面
 *
 * 页面布局 [LayoutSeekbarBinding]
 *
 * ViewModels []
 */
class LevelSelectDialogFragment(
    private val preFragment: Fragment,
    private val requestCode: Int,
    private val position: Int,
) : DialogFragment() {

    fun getInstance(selectLevel: Int, maxLv: Int) =
        this.apply {
            arguments = Bundle().apply {
                putInt(Constants.SELECT_LEVEL, selectLevel)
                putInt(Constants.MAX_LEVEL, maxLv)
            }
        }

    private lateinit var binding: LayoutSliderBinding
    private var selectLevel = 1
    private var maxLv = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            selectLevel = getInt(Constants.SELECT_LEVEL)
            maxLv = getInt(Constants.MAX_LEVEL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutSliderBinding.inflate(layoutInflater, container, false)

        binding.slider.apply {
            value = selectLevel.toFloat()
            valueFrom = 1.0f
            valueTo = maxLv.toFloat()
            binding.sliderText.text = selectLevel.toString()
            addOnSliderTouchListener(object :
                Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    dialog?.dismiss()
                }
            })
            addOnChangeListener { _, value, _ ->
                binding.sliderText.text = value.toInt().toString()
            }
        }

        setTargetFragment(preFragment, requestCode)

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        //获取已选择的数据，并返回
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt(Constants.SELECT_LEVEL, binding.slider.value.toInt())
        intent.putExtras(bundle)
        targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setGravity(Gravity.TOP)
            val width = ScreenUtil.getWidth() - 42.dp
            setLayout(width, 80.dp)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params = attributes
            params.y = position
            attributes = params
        }
    }

}