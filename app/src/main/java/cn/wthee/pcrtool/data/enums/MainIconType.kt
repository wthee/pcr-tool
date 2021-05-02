package cn.wthee.pcrtool.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainIconType(val type: Int, val icon: ImageVector) {
    /**
     * 关闭
     */
    DOWN(-1, Icons.Default.ExpandMore),

    /**
     * 主菜单
     */
    MAIN(0, Icons.Default.Menu),

    /**
     * 返回
     */
    BACK(1, Icons.Default.NavigateBefore),

    /**
     * 完成
     */
    OK(2, Icons.Default.Done),

    /**
     * 关闭
     */
    CLOSE(3, Icons.Default.ExpandMore),

    /**
     * 下载
     */
    DWONLOAD(4, Icons.Default.Download),

    /**
     * 下载
     */
    SETTING(5, Icons.Default.MiscellaneousServices),

    /**
     * 角色介绍
     */
    CHARACTER_INTRO(10, Icons.Default.AutoAwesome),

    /**
     * 技能循环
     */
    SKILL_LOOP(11, Icons.Default.AllInclusive),

    /**
     * RANK 选择
     */
    RANK_SELECT(12, Icons.Default.UnfoldMore),

    /**
     * 装嫩
     */
    EQUIP(13, Icons.Default.FitnessCenter),

    /**
     * 角色
     */
    CHARACTER(14, Icons.Default.Person),

    /**
     * 剧情活动
     */
    EVENT(15, Icons.Default.AutoStories),

    /**
     * 卡池
     */
    GACHA(16, Icons.Default.ReceiptLong),

    /**
     * 公会
     */
    GUILD(16, Icons.Default.LocalActivity),

    /**
     * 公会
     */
    LEADER(16, Icons.Default.EmojiEvents),

    /**
     * 团队战
     */
    CLAN(16, Icons.Default.Security),

}

fun getIcon(type: Int): ImageVector {
    for (item in MainIconType.values()) {
        if (item.type == type) return item.icon
    }
    return Icons.Default.Menu
}