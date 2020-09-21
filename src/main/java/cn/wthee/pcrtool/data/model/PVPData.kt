package cn.wthee.pcrtool.data.model


data class PVPData(
    val code: Int,
    val `data`: Data,
    val message: String,
    val version: String
)

data class ErrorData(
    val code: Int,
    val message: String,
    val version: String
)

data class Data(
    val page: Page,
    val result: List<Result>
)

data class Page(
    val hasMore: Boolean,
    val page: Int
)

data class Result(
    val atk: List<Atk>,
    val comment: Any,
    val def: List<Def>,
    val disliked: Boolean,
    val down: Int,
    val group: Boolean,
    val id: String,
    val iseditor: Boolean,
    val liked: Boolean,
    val `private`: Boolean,
    val up: Int,
    val updated: String
)

data class Atk(
    val equip: Boolean,
    val id: Int,
    val star: Int
)

data class Def(
    val equip: Boolean,
    val id: Int,
    val star: Int
)