package com.oh.app.data.store

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoreInfo(
    @SerializedName("CrtfcUpsoInfo")
    val StoreInfo: CrtfcUpsoInfo
) : Parcelable

@Parcelize
data class CrtfcUpsoInfo(
    @SerializedName("RESULT")
    val RESULT: RESULT,
    @SerializedName("list_total_count")
    val list_total_count: Int,
    @SerializedName("row")
    val row: List<Row>
) : Parcelable

@Parcelize
data class RESULT(
    @SerializedName("CODE")
    val CODE: String,
    @SerializedName("MESSAGE")
    val MESSAGE: String
) : Parcelable

@Parcelize
data class Row(
    @SerializedName("BIZCND_CODE")
    val BIZCND_CODE: String = "1",
    @SerializedName("BIZCND_CODE_NM")
    val BIZCND_CODE_NM: String = "1",
    @SerializedName("CGG_CODE")
    val CGG_CODE: String = "1",
    @SerializedName("CGG_CODE_NM")
    val CGG_CODE_NM: String = "1",
    @SerializedName("COB_CODE")
    val COB_CODE: String = "1",
    @SerializedName("COB_CODE_NM")
    val COB_CODE_NM: String = "1",
    @SerializedName("CRTFC_CHR_ID")
    val CRTFC_CHR_ID: String = "1",
    @SerializedName("CRTFC_CHR_NM")
    val CRTFC_CHR_NM: String = "1",
    @SerializedName("CRTFC_CLASS")
    val CRTFC_CLASS: String = "1",
    @SerializedName("CRTFC_GBN")
    val CRTFC_GBN: String = "1",
    @SerializedName("CRTFC_GBN_NM")
    val CRTFC_GBN_NM: String = "1",
    @SerializedName("CRTFC_SNO")
    val CRTFC_SNO: String = "1",
    @SerializedName("CRTFC_UPSO_MGT_SNO")
    val CRTFC_UPSO_MGT_SNO: Int = -1,
    @SerializedName("CRTFC_YMD")
    val CRTFC_YMD: String = "1",
    @SerializedName("CRTFC_YN")
    val CRTFC_YN: String = "1",
    @SerializedName("CRT_TIME")
    val CRT_TIME: String = "1",
    @SerializedName("CRT_USR")
    val CRT_USR: String = "1",
    @SerializedName("FOOD_MENU")
    val FOOD_MENU: String = "1",
    @SerializedName("GNT_NO")
    val GNT_NO: String = "1",
    @SerializedName("MAP_INDICT_YN")
    val MAP_INDICT_YN: String = "1",
    @SerializedName("OWNER_NM")
    val OWNER_NM: String = "1",
    @SerializedName("RDN_ADDR_CODE")
    val RDN_ADDR_CODE: String = "1",
    @SerializedName("RDN_CODE_NM")
    val RDN_CODE_NM: String = "1",
    @SerializedName("RDN_DETAIL_ADDR")
    val RDN_DETAIL_ADDR: String = "1",
    @SerializedName("TEL_NO")
    val TEL_NO: String = "1",
    @SerializedName("UPD_TIME")
    val UPD_TIME: String = "1",
    @SerializedName("UPSO_NM")
    val UPSO_NM: String = "1",
    @SerializedName("UPSO_SNO")
    val UPSO_SNO: String = "1",
    @SerializedName("USE_YN")
    val USE_YN: String = "1",
    @SerializedName("X_CNTS")
    val X_CNTS: String = "1",
    @SerializedName("Y_DNTS")
    val Y_DNTS: String = "1"
) : Parcelable