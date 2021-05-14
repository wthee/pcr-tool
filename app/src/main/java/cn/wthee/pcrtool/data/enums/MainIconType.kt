package cn.wthee.pcrtool.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainIconType(val type: Int, val icon: ImageVector) {
    /**
     * 关闭
     */
    DOWN(-1, Icons.Rounded.ExpandMore),

    /**
     * 主菜单
     */
    MAIN(0, Icons.Rounded.Add),

    /**
     * 返回
     */
    BACK(1, Icons.Rounded.NavigateBefore),

    /**
     * 完成
     */
    OK(2, Icons.Rounded.Done),

    /**
     * 关闭
     */
    CLOSE(3, Icons.Rounded.ExpandMore),

    /**
     * 下载
     */
    DWONLOAD(4, Icons.Rounded.Download),

    /**
     * 下载
     */
    SETTING(5, Icons.Rounded.MiscellaneousServices),

    /**
     * 下载
     */
    CHANGE_DATA(6, Icons.Rounded.SwapHoriz),

    /**
     * 未收藏
     */
    LOVE_LINE(7, Icons.Rounded.FavoriteBorder),

    /**
     * 收藏
     */
    LOVE_FILL(8, Icons.Rounded.Favorite),

    /**
     * 收藏
     */
    SHARE(9, Icons.Rounded.Share),

    /**
     * 角色介绍
     */
    CHARACTER_INTRO(10, Icons.Rounded.AutoAwesome),

    /**
     * 技能循环
     */
    SKILL_LOOP(11, Icons.Rounded.AllInclusive),

    /**
     * RANK 选择
     */
    RANK_SELECT(12, Icons.Rounded.UnfoldMore),

    /**
     * 装嫩
     */
    EQUIP(13, Icons.Rounded.FitnessCenter),

    /**
     * 角色
     */
    CHARACTER(14, Icons.Rounded.Grade),

    /**
     * 剧情活动
     */
    EVENT(15, Icons.Rounded.AutoStories),

    /**
     * 卡池
     */
    GACHA(16, Icons.Rounded.ReceiptLong),

    /**
     * 公会
     */
    GUILD(16, Icons.Rounded.LocalActivity),

    /**
     * 公会
     */
    LEADER(16, Icons.Rounded.EmojiEvents),

    /**
     * 团队战
     */
    CLAN(16, Icons.Rounded.Security),

    /**
     * 团队战阶段
     */
    CLAN_SECTION(17, Icons.Rounded.SignalCellularAlt),

    /**
     * 官网公告
     */
    NEWS(18, Icons.Rounded.Campaign),

    /**
     * 更新通知
     */
    NOTICE(19, Icons.Rounded.NotificationsNone),

    /**
     * 选中
     */
    PICK(20, Icons.Rounded.MyLocation),

    /**
     * 装备统计
     */
    EQUIP_CALC(21, Icons.Rounded.Calculate),

    /**
     * RANK 对比
     */
    RANK_COMPARE(22, Icons.Rounded.CompareArrows),

    /**
     * 日历
     */
    CALENDAR(23, Icons.Rounded.EventNote),

    /**
     * 竞技场查询
     */
    PVP_SEARCH(24, Icons.Rounded.ManageSearch),

    /**
     * 竞技场查询悬浮
     */
    PVP_SEARCH_WINDOW(25, Icons.Rounded.OpenInNew),

    /**
     * 新版本更新
     */
    APP_UPDATE(26, Icons.Rounded.Upcoming),

    /**
     * 群
     */
    GROUP(27, Icons.Rounded.GroupAdd),

    /**
     * 友链
     */
    FRIEND_LINK(28, Icons.Rounded.Link),

    /**
     * 添加对战信息
     */
    PVP_ADD(29, Icons.Rounded.Assistant),

    /**
     * 剩余时间
     */
    TIME_LEFT(30, Icons.Rounded.Schedule),

    /**
     * 倒计时
     */
    COUNTDOWN(31, Icons.Rounded.HourglassTop),

}