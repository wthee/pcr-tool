package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 角色详情模块
 */
enum class CharacterDetailModuleType(val id: Int, val titleId: Int) {
    UNKNOWN(299, R.string.unknown),
    CARD(300, R.string.character_card),
    COE(301, R.string.character_power),
    TOOLS(302, R.string.character_tool),
    STAR(303, R.string.title_rarity),
    LEVEL(304, R.string.title_unit_level),
    ATTR(305, R.string.character_attr),
    OTHER_TOOLS(306, R.string.character_other_tool),
    EQUIP(307, R.string.tool_equip),
    UNIQUE_EQUIP(308, R.string.tool_unique_equip),
    SKILL_LOOP(309, R.string.tip_skill_loop),
    SKILL(310, R.string.skill),
    UNIT_ICON(311, R.string.character_icon_info),
    LEADER(312, R.string.character_leader),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.id == value } ?: UNKNOWN
    }
}