package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * 振动
 * fixme 开关
 */
@Suppress("DEPRECATION")
class VibrateUtil(context: Context) {

    private val service = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val vibrateTime = 32L
    private val vibrateStrength = 32


    fun single() {
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

fun (() -> Unit).vibrate(arg: () -> Unit): () -> Unit = {
    arg()
    this()
}
