package cn.wthee.pcrtool.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Google Font Icon
 *
 * @param icon 矢量图标
 */
enum class MainIconType(val icon: ImageVector) {
    /**
     * 向下折叠
     */
    DOWN(Icons.Rounded.Remove),

    /**
     * 主菜单
     */
    MAIN(Icons.Rounded.Add),

    /**
     * 返回
     */
    BACK(Icons.Rounded.NavigateBefore),

    /**
     * 完成
     */
    OK(Icons.Rounded.Done),

    /**
     * 关闭
     */
    CLOSE(Icons.Rounded.Close),

    /**
     * 重置
     */
    RESET(Icons.Rounded.Refresh),

    /**
     * 下载
     */
    DWONLOAD(Icons.Rounded.Download),

    /**
     * 设置
     */
    SETTING(Icons.Rounded.MiscellaneousServices),

    /**
     * 搜索
     */
    SEARCH(Icons.Rounded.Search),

    /**
     * 数据切换
     */
    CHANGE_DATA(Icons.Rounded.SwapHoriz),

    /**
     * 未收藏
     */
    LOVE_LINE(Icons.Rounded.FavoriteBorder),

    /**
     * 收藏
     */
    LOVE_FILL(Icons.Rounded.Favorite),

    /**
     * 分享
     */
    SHARE(Icons.Rounded.Share),

    /**
     * 角色介绍
     */
    CHARACTER_INTRO(Icons.Rounded.Description),

    /**
     * 技能循环
     */
    SKILL_LOOP(Icons.Rounded.AllInclusive),

    /**
     * RANK 选择
     */
    RANK_SELECT(Icons.Rounded.UnfoldMore),

    /**
     * 装备
     */
    EQUIP(Icons.Rounded.FitnessCenter),

    /**
     * 角色
     */
    CHARACTER(Icons.Rounded.AutoAwesome),

    /**
     * 剧情活动
     */
    EVENT(Icons.Rounded.AutoStories),

    /**
     * 卡池
     */
    GACHA(Icons.Rounded.ReceiptLong),

    /**
     * 公会
     */
    GUILD(Icons.Rounded.Group),

    /**
     * 排行
     */
    LEADER(Icons.Rounded.EmojiEvents),

    /**
     * 团队战
     */
    CLAN(Icons.Rounded.Security),

    /**
     * 官网公告
     */
    NEWS(Icons.Rounded.Campaign),

    /**
     * 更新通知
     */
    NOTICE(Icons.Rounded.NotificationsNone),

    /**
     * 装备统计
     */
    EQUIP_CALC(Icons.Rounded.Calculate),

    /**
     * RANK 对比
     */
    RANK_COMPARE(Icons.Rounded.CompareArrows),

    /**
     * 日历
     */
    CALENDAR(Icons.Rounded.EventNote),

    /**
     * 竞技场查询
     */
    PVP_SEARCH(Icons.Rounded.ManageSearch),

    /**
     * 新版本更新
     */
    APP_UPDATE(Icons.Rounded.Upcoming),

    /**
     * 群
     */
    GROUP(Icons.Rounded.GroupAdd),

    /**
     * 友链
     */
    FRIEND_LINK(Icons.Rounded.Link),

    /**
     * 添加对战信息
     */
    PVP_ADD(Icons.Rounded.Assistant),

    /**
     * 剩余时间
     */
    TIME_LEFT(Icons.Rounded.Schedule),

    /**
     * 倒计时
     */
    COUNTDOWN(Icons.Rounded.HourglassTop),

    /**
     * 图片
     */
    IMAGE(Icons.Rounded.Image),

    /**
     * 浏览器
     */
    BROWSER(Icons.Rounded.OpenInBrowser),

    /**
     * 静流笔记
     */
    NOTE(Icons.Rounded.Stream),

    /**
     * 振动
     */
    VIBRATE(Icons.Rounded.Vibration),

    /**
     * 动画
     */
    ANIMATION(Icons.Rounded.Animation),

    /**
     * 删除
     */
    DELETE(Icons.Rounded.DeleteForever),

    /**
     * 数据更新
     */
    DB_DOWNLOAD(Icons.Rounded.CloudDownload),

    /**
     * 数据来源
     */
    DATA_SOURCE(Icons.Rounded.DataUsage),
}