package cn.wthee.pcrtool.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Api
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloseFullscreen
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.ContactSupport
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Looks4
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MiscellaneousServices
import androidx.compose.material.icons.rounded.NavigateBefore
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Signpost
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.SyncProblem
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Toc
import androidx.compose.material.icons.rounded.Token
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material.icons.rounded.Upcoming
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Google Font Icon
 *
 * @param icon 矢量图标
 */

enum class MainIconType(val icon: ImageVector) {
    /**
     * 向下
     */
    DOWN(Icons.Rounded.KeyboardArrowDown),

    /**
     * 向上
     */
    UP(Icons.Rounded.KeyboardArrowUp),

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
    RESET(Icons.Rounded.RestartAlt),

    /**
     * 同步
     */
    SYNC(Icons.Rounded.Sync),

    /**
     * 回到顶部
     */
    TOP(Icons.Rounded.ExpandLess),

    /**
     * 更多
     */
    MORE(Icons.Rounded.NavigateNext),

    /**
     * 功能
     */
    FUNCTION(Icons.Rounded.Api),

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
     * ex装备
     */
    EXTRA_EQUIP(Icons.Rounded.Token),

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
     * 模拟卡池
     */
    MOCK_GACHA(Icons.Rounded.Savings),

    /**
     * 模拟卡池抽取
     */
    MOCK_GACHA_PAY(Icons.Rounded.CreditCard),

    /**
     * 类型切换
     */
    CHANGE_FILTER_TYPE(Icons.Rounded.SwapVert),

    /**
     * 免费十连
     */
    FREE_GACHA(Icons.Rounded.Celebration),

    /**
     * 公会
     */
    GUILD(Icons.Rounded.Group),

    /**
     * 排行
     */
    LEADER(Icons.Rounded.EmojiEvents),

    /**
     * 公会战
     */
    CLAN(Icons.Rounded.Security),

    /**
     * 公会战阶段
     */
    CLAN_SECTION(Icons.Rounded.SignalCellularAlt),

    /**
     * 官网公告
     */
    NEWS(Icons.Rounded.Public),

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
     * 日历 - 进行中
     */
    CALENDAR_TODAY(Icons.Rounded.EventAvailable),

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
     * 接口请求异常
     */
    REQUEST_ERROR(Icons.Rounded.Warning),

    /**
     * 数据异常
     */
    DB_ERROR(Icons.Rounded.SyncProblem),

    /**
     * 反馈交流群
     */
    SUPPORT(Icons.Rounded.ContactSupport),

    /**
     * 添加对战信息
     */
    PVP_ADD(Icons.Rounded.PlaylistAdd),

    /**
     * 剩余时间
     */
    TIME_LEFT(Icons.Rounded.Schedule),

    /**
     * 倒计时
     */
    COUNTDOWN(Icons.Rounded.HourglassTop),

    /**
     * 浏览器
     */
    BROWSER(Icons.Rounded.Explore),

    /**
     * 悬浮窗
     */
    FLOAT(Icons.Rounded.CloseFullscreen),

    /**
     * 悬浮窗关闭
     */
    FLOAT_CLOSE(Icons.Rounded.ExitToApp),

    /**
     * 悬浮窗最小化
     */
    FLOAT_MIN(Icons.Rounded.Remove),

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
     * 动画
     */
    COLOR(Icons.Rounded.Palette),

    /**
     * 删除
     */
    DELETE(Icons.Rounded.DeleteForever),

    /**
     * 数据来源
     */
    DATA_SOURCE(Icons.Rounded.DataUsage),

    /**
     * 推特信息
     */
    TWEET(Icons.Rounded.Timeline),

    /**
     * 漫画信息
     */
    COMIC(Icons.Rounded.Looks4),

    /**
     * 漫画目录
     */
    COMIC_NAV(Icons.Rounded.Toc),

    /**
     * 帮助
     */
    HELP(Icons.Rounded.Help),

    /**
     * 信息
     */
    INFO(Icons.Rounded.Info),

    /**
     * 随机掉落地区
     */
    RANDOM_AREA(Icons.Rounded.TrackChanges),

    /**
     * 项目详情
     */
    GITHUB_PROJECT(Icons.Rounded.DataObject),

    /**
     * 发布详情
     */
    COOLAPK_APP_STORE(Icons.Rounded.Verified),

    /**
     * 编辑
     */
    EDIT_TOOL(Icons.Rounded.Edit),

    /**
     * 生日日程
     */
    BIRTHDAY(Icons.Rounded.Cake),

    /**
     * 预览模型
     */
    PREVIEW_UNIT_SPINE(Icons.Rounded.RemoveRedEye),

    /**
     * 图片
     */
    PREVIEW_IMAGE(Icons.Rounded.Image),

    /**
     * 召唤物
     */
    SUMMON(Icons.Rounded.Pets),

    /**
     * 技能形态
     */
    CHARACTER_NORMAL_SKILL(Icons.Outlined.Category),

    /**
     * 技能形态
     */
    CHARACTER_CUTIN_SKILL(Icons.Rounded.Category),

    /**
     * ex装备掉落信息
     */
    EXTRA_EQUIP_DROP(Icons.Rounded.Map),

    /**
     * 筛选
     */
    FILTER(Icons.Rounded.FilterAlt),

    /**
     * 降序
     */
    SORT_DESC(Icons.Rounded.KeyboardArrowDown),

    /**
     * 升序
     */
    SORT_ASC(Icons.Rounded.KeyboardArrowUp),

    /**
     * 不排序
     */
    SORT_NULL(Icons.Rounded.UnfoldMore),

    /**
     * 网站收藏
     */
    WEBSITE_BOOKMARK(Icons.Rounded.Bookmarks),

    /**
     * 角色评级
     */
    LEADER_TIER(Icons.Rounded.WorkspacePremium),

    /**
     * PC
     */
    BROWSER_PC(Icons.Rounded.DesktopWindows),

    /**
     * 手机
     */
    BROWSER_PHONE(Icons.Rounded.PhoneAndroid),

    /**
     * 手机
     */
    BROWSER_APP(Icons.Rounded.Android),

    /**
     * 主线地图
     */
    ALL_QUEST(Icons.Rounded.Signpost),

    /**
     * 日期范围
     */
    DATE_RANGE_NONE(Icons.Rounded.CalendarToday),

    /**
     * 已选择日期范围
     */
    DATE_RANGE_PICKED(Icons.Rounded.DateRange),

    /**
     * box
     */
    BOX(Icons.Rounded.Archive),

    /**
     * 专用装备
     */
    UNIQUE_EQUIP(Icons.Rounded.Handyman),

}