package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication.Companion.URL_DOMAIN
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids

/**
 * 图片资源地址
 */
class ImageRequestHelper {

    var type = "cn"

    init {
        type = when (MainActivity.regionType) {
            2 -> "cn"
            3 -> "tw"
            else -> "jp"
        }
    }

    companion object {

        fun getInstance() = ImageRequestHelper()

        private var RESOURCE_PREFIX_URL = "https://${URL_DOMAIN}/redive/"
        private var RESOURCE = "/resource/"

        //图片格式
        const val WEBP = ".webp"
        const val PNG = ".png"

        //角色卡面
        const val CARD_PROFILE = "card/profile/"

        //角色完整卡面
        const val CARD_FULL = "card/full/"

        //现实角色卡面
        const val CARD_ACTUAL_PROFILE = "card/actual_profile/"

        //角色剧情卡面
        const val CARD_STORY = "card/story/"

        //装备图标
        const val UNKNOWN_EQUIP_ID = 999999
        const val ICON_EQUIPMENT = "icon/equipment/"
        const val ICON_EXTRA_EQUIPMENT = "icon/ex_equipment/"
        const val ICON_EXTRA_EQUIPMENT_CATEGORY = "icon/ex_equipment/category/"
        const val ICON_EXTRA_EQUIPMENT_TRAVEL_MAP = "icon/ex_equipment/map/"

        //角色图标
        const val ICON_UNIT = "icon/unit/"

        //技能图标
        const val ICON_SKILL = "icon/skill/"

        //剧情活动 banner
        const val EVENT_BANNER = "event/banner/"
        const val EVENT_TEASER = "event/teaser/"

        //剧情活动剧情
        const val EVENT_STORY = "event/story/"

        //图片资源地址
        private var OTHER_RESOURCE_URL = "https://${URL_DOMAIN}/resource/"

        //漫画地址
        var COMIC4 = OTHER_RESOURCE_URL + "comic/"

    }

    /**
     * 获取资源地址前缀
     * @param forceJpType 使用日服图片资源
     */
    fun getUrl(resUrl: String, id: Any, forceJpType: Boolean = true) =
        RESOURCE_PREFIX_URL + (if (forceJpType) "jp" else type) + RESOURCE + resUrl + id.toString() + WEBP

    //获取装备图标
    fun getEquipPic(id: Int) = if (id == UNKNOWN_EQUIP_ID) {
        R.drawable.unknown_item
    } else {
        RESOURCE_PREFIX_URL + type + RESOURCE + ICON_EQUIPMENT + id.toString() + WEBP
    }

    /**
     * 获取角色卡面 urls
     *
     * @param unitId 角色编号
     */
    fun getAllPicUrl(unitId: Int, actualId: Int?): ArrayList<String> {
        val list = arrayListOf<String>()
        if (r6Ids.contains(unitId)) {
            list.add(getUrl(CARD_FULL, getStarId(unitId, 6)))
        }
        list.add(getUrl(CARD_FULL, getStarId(unitId, 3)))
        list.add(getUrl(CARD_PROFILE, getStarId(unitId, 1)))
        if (actualId != null) {
            list.add(getUrl(CARD_ACTUAL_PROFILE, actualId))
        }
        return list
    }

    /**
     * 获取星级最高的角色图片
     *
     * @param unitId 角色编号
     */
    fun getMaxCardUrl(unitId: Int): String {
        if (r6Ids.contains(unitId)) {
            return getUrl(CARD_FULL, getStarId(unitId, 6))
        }
        return getUrl(CARD_FULL, getStarId(unitId, 3))
    }

    /**
     * 获取星级最高的角色图标
     *
     * @param unitId 角色编号
     */
    fun getMaxIconUrl(unitId: Int): String {
        if (r6Ids.contains(unitId)) {
            return getUrl(ICON_UNIT, getStarId(unitId, 6))
        }
        return getUrl(ICON_UNIT, getStarId(unitId, 3))
    }

    /**
     * 获取角色图标
     *
     * @param unitId 角色编号
     * @param star 星级
     */
    fun getUnitIconUrl(unitId: Int, star: Int): String {
        return getUrl(ICON_UNIT, getStarId(unitId, star))
    }

    /**
     * 获取星级 [star] id
     */
    private fun getStarId(unitId: Int, star: Int): String {
        return try {
            val idStr = unitId.toString()
            idStr.substring(0, 4) + star + idStr[idStr.lastIndex]
        } catch (e: Exception) {
            ""
        }
    }

}

