package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import cn.wthee.pcrtool.ui.MainActivity

/**
 * 振动
 */
@Suppress("DEPRECATION")
class VibrateUtil(context: Context) {

    private val service = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val vibrateTime = 32L
    private val vibrateStrength = 32

    //是否开启振动
    private val vibrateOn = MainActivity.vibrateOn


    fun single() {
        if (vibrateOn) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    service.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    service.vibrate(VibrationEffect.createOneShot(vibrateTime, vibrateStrength))
                }
                else -> {
                    service.vibrate(vibrateTime)
                }
            }
        }
    }

    fun done() {
        if (vibrateOn) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    service.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    service.vibrate(VibrationEffect.createOneShot(vibrateTime, vibrateStrength))
                }
                else -> {
                    service.vibrate(vibrateTime)
                }
            }
        }
    }
}

fun (() -> Unit).vibrate(arg: () -> Unit): () -> Unit = {
    arg()
    this()
}
