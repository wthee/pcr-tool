package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.data.model.CharacterPicData

/**
 * 角色 id 工具
 */
object CharacterIdUtil {

    /**
     * 获取角色卡面 urls
     */
    fun getAllPicUrl(id: Int, r6Id: Boolean): ArrayList<CharacterPicData> {
        val list = arrayListOf<CharacterPicData>()
        if (r6Id) {
            list.add(
                CharacterPicData(
                    "6 星",
                    Constants.CHARACTER_FULL_URL + getStarId(id, 6) + Constants.WEBP
                )
            )
        }
        list.add(
            CharacterPicData(
                "3 星",
                Constants.CHARACTER_FULL_URL + getStarId(id, 3) + Constants.WEBP
            )
        )
        list.add(
            CharacterPicData(
                "初始",
                Constants.CHARACTER_URL + getStarId(id, 1) + Constants.WEBP
            )
        )
        if (!Constants.notExistsIDs.contains(getFixedId(id))) {
            list.add(
                CharacterPicData(
                    "现实",
                    Constants.Reality_CHARACTER_URL + getFixedId(id) + Constants.WEBP
                )
            )
        }
        return list
    }

    /**
     * 获取角色图片 urls
     */
    fun getAllIconUrl(uid: Int, r6Id: Boolean): ArrayList<String> {
        val list = arrayListOf<String>()

        if (r6Id) {
            list.add(Constants.UNIT_ICON_URL + getStarId(uid, 6) + Constants.WEBP)
        }
        list.add(Constants.UNIT_ICON_URL + getStarId(uid, 3) + Constants.WEBP)
        list.add(Constants.UNIT_ICON_URL + getStarId(uid, 1) + Constants.WEBP)
        return list
    }

    /**
     * 获取星级 [star] id
     */
    private fun getStarId(uid: Int, star: Int): String {
        val idStr = uid.toString()
        return idStr.substring(0, 4) + star + idStr[idStr.lastIndex]
    }

    /**
     * 去除无效id
     */
    private fun getFixedId(id: Int) = id + if (Constants.errorIDs.contains(id)) 31 else 30


}