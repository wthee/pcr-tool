package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.view.EquipmentMaxData
import cn.wthee.pcrtool.database.view.UniqueEquipmentMaxData
import kotlinx.coroutines.launch


class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentMaxData>>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData>()
    var refresh = MutableLiveData<Boolean>()

    //获取装备列表
    fun getEquips(asc: Boolean, name: String) {
        viewModelScope.launch {
            val data = equipmentRepository.getAllEquipments(name)
            refresh.postValue(false)
            if (asc) {
                data.sortedBy { it.promotionLevel }
            } else {
                data.sortedByDescending { it.promotionLevel }
            }
            equipments.postValue(data)
        }
    }

    //专武信息
    fun getUniqueEquipInfos(uid: Int) {
        viewModelScope.launch {
            val data = equipmentRepository.getUniqueEquipInfos(uid)
            uniqueEquip.postValue(data)
        }
    }

    //获取装备类型
    suspend fun getTypes() = equipmentRepository.getEquipTypes()

    //TODO 分页加载
//    val allEquips = Pager(
//        PagingConfig(
//            /**
//             * A good page size is a value that fills at least a few screens worth of content on a
//             * large device so the User is unlikely to see a null item.
//             * You can play with this constant to observe the paging behavior.
//             *
//             * It's possible to vary this with list device size, but often unnecessary, unless a
//             * user scrolling on a large device is expected to scroll through items more quickly
//             * than a small device, such as when the large device uses a grid layout of items.
//             */
//            pageSize = 60,
//
//            /**
//             * If placeholders are enabled, PagedList will report the full size but some items might
//             * be null in onBind method (PagedListAdapter triggers a rebind when data is loaded).
//             *
//             * If placeholders are disabled, onBind will never receive null but as more pages are
//             * loaded, the scrollbars will jitter as new pages are loaded. You should probably
//             * disable scrollbars if you disable placeholders.
//             */
//            enablePlaceholders = true,
//
//            /**
//             * Maximum number of items a PagedList should hold in memory at once.
//             *
//             * This number triggers the PagedList to start dropping distant pages as more are loaded.
//             */
//            maxSize = 200
//        )
//    ) {
//        equipmentRepository.getPagingEquipments(name = "")
//    }.flow
}
