package cn.wthee.pcrtool.utils

import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * 解压工具
 */
object UnzippedUtil {

    private const val BUFFER = 1024
    private const val EXT = ".br"

    /**
     * 文件解压
     */
    @Throws(IOException::class)
    fun deCompress(file: File, delete: Boolean) {
        val input = FileInputStream(file)
        val os =
            FileOutputStream(file.path.replace(EXT, ""))
        val bcis = BrotliCompressorInputStream(input)
        var count: Int
        val data = ByteArray(BUFFER)
        while (bcis.read(data, 0, BUFFER).also { count = it } != -1) {
            os.write(data, 0, count)
        }
        bcis.close()
        os.flush()
        os.close()
        input.close()
        if (delete) {
            file.delete()
        }
    }
}