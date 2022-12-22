package com.oh.app.data.mart

/**
 * 마트이름과 대형마트, 전통시장 구분하는 data class
 */
data class MartRow(
    val martName: String,
    val martType: String
)