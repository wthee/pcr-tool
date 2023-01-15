package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 立绘类型
 */
enum class AllPicsType(val type: Int) {
    CHARACTER(0),
    STORY(1);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: CHARACTER
    }
}

/**
 * 活动类型
 */
enum class EventType(val type: Int) {
    UNKNOWN(0),
    IN_PROGRESS(1),
    COMING_SOON(2);
}

/**
 * 角色单位类型
 */
enum class UnitType(val type: Int) {
    CHARACTER(0),
    CHARACTER_SUMMON(1),
    ENEMY(2),
    ENEMY_SUMMON(3);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: CHARACTER
    }
}

/**
 * Rank 选择类型
 */
enum class RankSelectType(val type: Int) {
    DEFAULT(0),
    LIMIT(1);
}

/**
 * 卡池类型
 */
enum class GachaType(val stringId: Int) {
    UNKNOWN(R.string.unknown),
    LIMIT(R.string.type_limit),
    RE_LIMIT(R.string.limit_re),
    NORMAL(R.string.type_normal),
    RE_NORMAL(R.string.normal_re),
    FES(R.string.fes),
    ANNIV(R.string.anv);
}

/**
 *菜单
 */
enum class ToolMenuType(val id: Int) {
    CHARACTER(200),
    EQUIP(201),
    GUILD(202),
    CLAN(203),
    RANDOM_AREA(204),
    GACHA(205),
    EVENT(206),
    NEWS(207),
    FREE_GACHA(208),
    PVP_SEARCH(209),
    LEADER(210),
    TWEET(211),
    ALL_SKILL(213),
    ALL_EQUIP(214),
    MOCK_GACHA(215),
    BIRTHDAY(216),
    CALENDAR_EVENT(217),
    EXTRA_EQUIP(218),
    TRAVEL_AREA(219),
    WEBSITE(220),
    LEADER_TIER(221);


    companion object {
        fun getByValue(value: Int) = values()
            .find { it.id == value }
    }
}

/**
 * 站位
 */
enum class PositionType {
    UNKNOWN,
    POSITION_0_299,
    POSITION_300_599,
    POSITION_600_999;

    companion object {
        fun getPositionType(position: Int) = when (position) {
            in 1..299 -> POSITION_0_299
            in 300..599 -> POSITION_300_599
            in 600..9999 -> POSITION_600_999
            else -> UNKNOWN
        }
    }
}

/**
 * 技能类型
 */
enum class SkillType {
    ALL,
    NORMAL,
    SP
}

/**
 * 属性值类型
 */
enum class AttrValueType {
    INT,
    DOUBLE,
    PERCENT
}

/**
 * 首页
 */
enum class OverviewType(val id: Int) {
    CHARACTER(0),
    EQUIP(1),
    TOOL(2),
    NEWS(3),
    IN_PROGRESS_EVENT(4),
    COMING_SOON_EVENT(5);

    companion object {
        fun getByValue(value: Int) = values()
            .find { it.id == value } ?: CHARACTER
    }
}

/**
 * 设置枚举
 */
enum class SettingSwitchType {
    /**
     * 振动设置
     */
    VIBRATE,
    /**
     * 动画效果设置
     */
    ANIMATION,
    /**
     * 动态色彩设置
     */
    DYNAMIC_COLOR
}


/**
 * 公告
 */
enum class NewsType(val stringId: Int) {
    UPDATE(R.string.update),
    SYSTEM(R.string.system),
    NEWS(R.string.tool_news),
    EVENT(R.string.event),
    SHOP(R.string.shop),
    LOCAL(R.string.local_note);
}

/**
 * 查询关键词
 */
enum class KeywordType(val type: Int) {
    NEWS(1),
    TWEET(2);
}