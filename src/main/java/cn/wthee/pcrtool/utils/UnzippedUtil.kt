package cn.wthee.pcrtool.utils

import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream
import java.io.*

object UnzippedUtil {
    /**缓冲字节 */
    const val BUFFER = 1024

    /**后缀名 */
    const val EXT = ".br"

    /**
     * 文件解压缩
     * @param path 文件路径
     * @param delete 是否删除源文件
     */
    @Throws(IOException::class)
    fun deCompress(path: String, delete: Boolean) {
        val file = File(path)
        deCompress(file, delete)
    }

    /**
     * 文件解压缩
     * @param file 压缩文件
     * @param delete 是否删除源文件
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deCompress(file: File, delete: Boolean) {
        val fis = FileInputStream(file)
        val fos =
            FileOutputStream(file.path.replace(EXT, ""))
        deCompress(fis, fos)
        fos.flush()
        fos.close()
        fis.close()
        if (delete) {
            file.delete()
        }
    }

    /**
     * 解压缩
     * @param input 输入流
     * @param os 输出流
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun deCompress(input: InputStream, os: OutputStream) {
        val bcis = BrotliCompressorInputStream(input)
        var count: Int
        val data = ByteArray(BUFFER)
        while (bcis.read(data, 0, BUFFER).also { count = it } != -1) {
            os.write(data, 0, count)
        }
        bcis.close()
    }

}