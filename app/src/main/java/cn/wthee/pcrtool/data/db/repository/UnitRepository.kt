package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.UnitDao
import cn.wthee.pcrtool.data.model.FilterCharacter
import javax.inject.Inject

/**
 * 角色 Repository
 *
 * @param unitDao
 */
class UnitRepository @Inject constructor(private val unitDao: UnitDao) {

    suspend fun getInfoAndData(filter: FilterCharacter, guildName: String) = unitDao.getInfoAndData(
        filter.sortType.type,
        if (filter.asc) "asc" else "desc",
        filter.name,
        filter.position()[0],
        filter.position()[1],
        filter.atk,
        guildName,
        if (filter.all) 1 else 0,
        if (filter.r6) 1 else 0,
        filter.starIds,
        filter.type
    )

    suspend fun getInfoAndData(limit: Int) = unitDao.getInfoAndData(limit)

    suspend fun getInfoPro(unitId: Int) = unitDao.getInfoPro(unitId)

    suspend fun getCharacterByPosition(start: Int, end: Int) =
        unitDao.getCharacterByPosition(start, end)

    suspend fun getCharacterByIds(unitIds: List<Int>) = unitDao.getCharacterByIds(unitIds)


    suspend fun getEquipmentIds(unitId: Int, rank: Int) =
        unitDao.getRankEquipment(unitId, rank)

    suspend fun getRankStatus(unitId: Int, rank: Int) = unitDao.getRankStatus(unitId, rank)

    suspend fun getRarity(unitId: Int, rarity: Int) = unitDao.getRarity(unitId, rarity)

    suspend fun getMaxRank(unitId: Int) = unitDao.getMaxRank(unitId)

    suspend fun getMaxRarity(unitId: Int) = unitDao.getMaxRarity(unitId)

    suspend fun getGuilds() = unitDao.getGuilds()

    suspend fun getGuildAddMembers(guildId: Int) = unitDao.getGuildAddMembers(guildId)

    suspend fun getR6Ids() = unitDao.getR6Ids()

    suspend fun getCharacterStoryStatus(unitId: Int) = unitDao.getCharacterStoryStatus(unitId)

    suspend fun getMaxLevel() = unitDao.getMaxLevel()

    suspend fun getRankBonus(rank: Int, unitId: Int) = unitDao.getRankBonus(rank, unitId)

}