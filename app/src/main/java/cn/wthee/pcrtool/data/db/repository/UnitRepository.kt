package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.UnitDao
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter

/**
 * 角色 Repository
 *
 * 数据来源 [UnitDao]
 */
class UnitRepository(private val unitDao: UnitDao) {

    fun getInfoAndData(
        sortType: SortType,
        asc: Boolean,
        name: String,
        filter: FilterCharacter
    ) = unitDao.getInfoAndData(
        sortType.type,
        if (asc) "asc" else "desc",
        name,
        filter.position()[0],
        filter.position()[1],
        filter.atk,
        filter.guild,
        if (filter.all) 1 else 0,
        if (filter.r6) 1 else 0,
        filter.starIds
    )

    suspend fun getInfoAndDataCount(name: String, filter: FilterCharacter) =
        unitDao.getInfoAndDataCount(
            name,
            filter.position()[0],
            filter.position()[1],
            filter.atk,
            filter.guild,
            if (filter.all) 1 else 0,
            if (filter.r6) 1 else 0,
            filter.starIds
        )

    suspend fun getInfoPro(uid: Int) = unitDao.getInfoPro(uid)

    suspend fun getCharacterByPosition(start: Int, end: Int) =
        unitDao.getCharacterByPosition(start, end)

    suspend fun getEquipmentIds(unitId: Int, rank: Int) =
        unitDao.getRankEquipment(unitId, rank)

    suspend fun getRankStatus(unitId: Int, rank: Int) = unitDao.getRankStatus(unitId, rank)

    suspend fun getRarity(unitId: Int, rarity: Int) = unitDao.getRarity(unitId, rarity)

    suspend fun getMaxRank(id: Int) = unitDao.getMaxRank(id)

    suspend fun getMaxRarity(id: Int) = unitDao.getMaxRarity(id)

    suspend fun getGuilds() = unitDao.getGuilds()

    suspend fun getR6Ids() = unitDao.getR6Ids()

    suspend fun getItemDropInfos(unitId: Int) = unitDao.getItemDropInfos(unitId)

    suspend fun getCharacterStoryStatus(unitId: Int) = unitDao.getCharacterStoryStatus(unitId)

    suspend fun getMaxLevel() = unitDao.getMaxLevel()

    companion object {

        fun getInstance(unitDao: UnitDao) = UnitRepository(unitDao)
    }
}