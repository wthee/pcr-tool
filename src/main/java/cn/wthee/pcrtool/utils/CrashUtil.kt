package cn.wthee.pcrtool.utils

/**
 * 异常回调
 */
abstract class CrashHandleCallback {
    /**
     * Crash处理.
     *
     * @param crashType 错误类型：CRASHTYPE_JAVA，CRASHTYPE_NATIVE，CRASHTYPE_U3D ,CRASHTYPE_ANR
     * @param errorType 错误的类型名
     * @param errorMessage 错误的消息
     * @param errorStack 错误的堆栈
     * @return 返回额外的自定义信息上报
     */
    abstract fun onCrashHandleStart(
        crashType: Int, errorType: String?,
        errorMessage: String?, errorStack: String?
    ): Map<String?, String?>?

    /**
     * Crash处理.
     *
     * @param crashType 错误类型：CRASHTYPE_JAVA，CRASHTYPE_NATIVE，CRASHTYPE_U3D ,CRASHTYPE_ANR
     * @param errorType 错误的类型名
     * @param errorMessage 错误的消息
     * @param errorStack 错误的堆栈
     * @return byte[] 额外的2进制内容进行上报
     */
    abstract fun onCrashHandleStart2GetExtraDatas(
        crashType: Int, errorType: String?,
        errorMessage: String?, errorStack: String?
    ): ByteArray?

    companion object {
        const val CRASHTYPE_JAVA_CRASH = 0 // Java crash
        const val CRASHTYPE_JAVA_CATCH = 1 // Java caught exception
        const val CRASHTYPE_NATIVE = 2 // Native crash
        const val CRASHTYPE_U3D = 3 // Unity error
        const val CRASHTYPE_ANR = 4 // ANR
        const val CRASHTYPE_COCOS2DX_JS = 5 // Cocos JS error
        const val CRASHTYPE_COCOS2DX_LUA = 6 // Cocos Lua error
    }
}