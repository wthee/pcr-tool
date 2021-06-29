package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.enums.MainIconType

data class TweetButtonData(
    val text: String,
    val iconType: MainIconType,
    val action: () -> Unit
)
