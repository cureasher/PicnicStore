package com.oh.app.data.mart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 서울시 마트 물가정보 data class
 */
@Parcelize
data class MartData(
    @SerializedName("ListNecessariesPricesService")
    val martData: ListNecessariesPricesService
) :  Parcelable

@Parcelize
data class ListNecessariesPricesService(
    @SerializedName("RESULT")
    val result: RESULT,
    @SerializedName("list_total_count")
    val listTotalCount: Int,
    @SerializedName("row")
    val martInfoList: List<Row>
) : Parcelable

@Parcelize
data class RESULT(
    @SerializedName("CODE")
    val code: String,
    @SerializedName("MESSAGE")
    val message: String
) : Parcelable

@Parcelize
data class Row(
    @SerializedName("ADD_COL")
    val addCol: String,
    @SerializedName("A_NAME")
    val aName: String,
    @SerializedName("A_PRICE")
    val aPrice: String,
    @SerializedName("A_SEQ")
    val aSeq: Int,
    @SerializedName("A_UNIT")
    val AUnit: String,
    @SerializedName("M_GU_CODE")
    val mGuCode: String,
    @SerializedName("M_GU_NAME")
    val mGuName: String,
    @SerializedName("M_NAME")
    val mName: String,
    @SerializedName("M_SEQ")
    val mSeq: Int,
    @SerializedName("M_TYPE_CODE")
    val mTypeCode: String,
    @SerializedName("M_TYPE_NAME")
    val mTypeName: String,
    @SerializedName("P_DATE")
    val pDate: String,
    @SerializedName("P_SEQ")
    val pSeq: Int,
    @SerializedName("P_YEAR_MONTH")
    val pYearMonth: String
) : Parcelable