package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 卡池类型
 */
enum class GachaType(val stringId: Int) {
    UNKNOWN(R.string.unknown),
    LIMIT(R.string.type_limit),
    RE_LIMIT(R.string.limit_re),
    RE_LIMIT_PICK(R.string.limit_re_pick),
    LIMIT_PICK(R.string.limit_pick),
    NORMAL(R.string.type_normal),
    RE_NORMAL(R.string.normal_re),
    FES(R.string.fes),
    ANNIV(R.string.anv);
}