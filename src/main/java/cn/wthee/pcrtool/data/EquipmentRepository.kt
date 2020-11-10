package cn.wthee.pcrtool.data

import cn.wthee.pcrtool.data.model.FilterEquipment


//角色数据Repository

class EquipmentRepository(private val equipmentDao: EquipmentDao) {


    //获取角色Rank所需装备具体属性
    suspend fun getEquipmentDatas(eids: List<Int>) = equipmentDao.getEquipmentDatas(eids)

    //获取装备具体属性
    suspend fun getEquipmentData(eid: Int) = equipmentDao.getEquipInfos(eid)

    //装备类型
    suspend fun getEquipTypes() = equipmentDao.getEquipTypes()

    //所有装备信息
    fun getPagingEquipments(name: String, filter: FilterEquipment) =
        equipmentDao.getPagingEquipments(
            filter.type, name,
            if (filter.all) 1 else 0,
            filter.starIds
        )

    suspend fun getEquipmentCount(name: String, filter: FilterEquipment) =
        equipmentDao.getEquipmentCount(
            filter.type, name,
            if (filter.all) 1 else 0,
            filter.starIds
        )

    //获取装备掉落区域
    suspend fun getEquipDropAreas(eid: Int) = equipmentDao.getEquipDropAreas(eid)

    //装备碎片信息
    suspend fun getEquipmentCraft(eid: Int) = equipmentDao.getEquipmentCraft(eid)

    //专武信息
    suspend fun getUniqueEquipInfos(uid: Int) = equipmentDao.getUniqueEquipInfos(uid)

    companion object {

        @Volatile
        private var instance: EquipmentRepository? = null

        fun getInstance(equipmentDao: EquipmentDao) =
            instance ?: synchronized(this) {
                instance ?: EquipmentRepository(equipmentDao).also { instance = it }
            }
    }
}