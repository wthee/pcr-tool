package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids

class ImageResourceHelper() {

    var type = "cn"

    init {
        type = when (getRegion()) {
            2 -> "cn"
            3 -> "tw"
            else -> "jp"
        }
    }

    companion object {

        fun getInstance() = ImageResourceHelper()

        private var RESOURCE_PREFIX_URL = "https://wthee.xyz/redive/"
        private var RESOURCE = "/resource/"

        //图片格式
        const val WEBP = ".webp"
        const val PNG = ".png"

        //角色卡面
        val CARD_PROFILE = "card/profile/"

        //角色完整卡面
        val CARD_FULL = "card/full/"

        //现实角色卡面
        var CARD_ACTUAL_PROFILE = "card/actual_profile/"

        //角色剧情卡面
        val CARD_STORY = "card/story/"

        //装备图标
        var UNKNOWN_EQUIP_ID = 999999
        var ICON_EQUIPMENT = "icon/equipment/"

        //角色图标
        var ICON_UNIT = "icon/unit/"

        //技能图标
        var ICON_SKILL = "icon/skill/"

        //剧情活动 banner
        var EVENT_BANNER = "event/banner/"

        //剧情活动剧情
        var EVENT_STORY = "event/story/"

        //图片资源地址
        private const val OTHER_RESOURCE_URL = "https://wthee.xyz/resource/"

        //漫画地址
        val COMIC4 = OTHER_RESOURCE_URL + "comic/"

        //现实图片有误的角色编号
        val errorIDs = arrayListOf(
            101001,
            101301,
            101501,
            102201,
            102801,
            103801,
            104501,
            104601,
            105401,
        )

        //无现实图片角色
        val notExistsIDs = arrayListOf(
            109731, 109831, 109931, 109231, 109331, 109431
        )
    }

    //获取资源地址前缀
    fun getUrl(resUrl: String, id: Any) =
        RESOURCE_PREFIX_URL + type + RESOURCE + resUrl + id.toString() + WEBP

    /**
     * 获取角色卡面 urls
     *
     * @param unitId 角色编号
     */
    fun getAllPicUrl(unitId: Int): ArrayList<String> {
        val list = arrayListOf<String>()
        if (r6Ids.contains(unitId)) {
            list.add(getUrl(CARD_FULL, getStarId(unitId, 6)))
        }
        list.add(getUrl(CARD_FULL, getStarId(unitId, 3)))
        list.add(getUrl(CARD_PROFILE, getStarId(unitId, 1)))
        if (!notExistsIDs.contains(getFixedId(unitId))) {
            list.add(getUrl(CARD_ACTUAL_PROFILE, getFixedId(unitId)))
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
     * @param r6Id 是否已解放六星
     */
    fun getMaxIconUrl(unitId: Int, r6Id: Boolean): String {
        if (r6Id) {
            return getUrl(ICON_UNIT, getStarId(unitId, 6))

        }
        return getUrl(ICON_UNIT, getStarId(unitId, 3))
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

    /**
     * 去除无效id
     */
    private fun getFixedId(unitId: Int) =
        unitId + if (errorIDs.contains(unitId)) 31 else 30

}