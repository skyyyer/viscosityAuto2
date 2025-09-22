package com.hm.viscosityauto.model

data class UploadBean(
    val details: List<Detail>,
    val dwmc: String = "",
    val password: String = "",
    val username: String = "",
    val yqbh: String = ""
)

data class Detail(
    val jiancedidian: String = "",
    val jiancejieguo: String = "",
    val jianceren: String = "",
    val jianceriqi: String = "",
    val jiancexiangmu: String = "",
    val jiancezhi: String = "",
    val lianxidianhua: String = "",
    val shanghumingcheng: String = "",
    val yangpinbianhao: String = "",
    val yangpinmingcheng: String = ""
)
