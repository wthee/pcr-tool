package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable


/**
 * 关键词
 */
@Serializable
data class KeywordData(
    val desc: String,
    val id: Int,
    val keyword: String
)