package com.oh.app.data.store

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 서울 식당 위치 정보 data class
 */
@Parcelize
data class StoreInfo(
    @SerializedName("CrtfcUpsoInfo")
    val storeInfo: CrtfcUpsoInfo
) : Parcelable

@Parcelize
data class CrtfcUpsoInfo(
    @SerializedName("RESULT")
    val result: RESULT,
    @SerializedName("list_total_count")
    val listTotalCount: Int,
    @SerializedName("row")
    val row: List<Row>
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
    @SerializedName("BIZCND_CODE")
    val bizcndCode: String = "1",
    @SerializedName("BIZCND_CODE_NM")
    val bizcndCodnNm: String = "1",
    @SerializedName("CGG_CODE")
    val cggCode: String = "1",
    @SerializedName("CGG_CODE_NM")
    val cggCodeNm: String = "1",
    @SerializedName("COB_CODE")
    val cobCode: String = "1",
    @SerializedName("COB_CODE_NM")
    val cobCodeNm: String = "1",
    @SerializedName("CRTFC_CHR_ID")
    val crtfcChrId: String = "1",
    @SerializedName("CRTFC_CHR_NM")
    val crtfcChrNm: String = "1",
    @SerializedName("CRTFC_CLASS")
    val crtfcClass: String = "1",
    @SerializedName("CRTFC_GBN")
    val crtfcGbn: String = "1",
    @SerializedName("CRTFC_GBN_NM")
    val crtfcGbnNm: String = "1",
    @SerializedName("CRTFC_SNO")
    val crtfcSno: String = "1",
    @SerializedName("CRTFC_UPSO_MGT_SNO")
    val crtfcUpsoMgtSno: Int = -1,
    @SerializedName("CRTFC_YMD")
    val crtfcYmd: String = "1",
    @SerializedName("CRTFC_YN")
    val crtfcYn: String = "1",
    @SerializedName("CRT_TIME")
    val crtTime: String = "1",
    @SerializedName("CRT_USR")
    val crtUsr: String = "1",
    @SerializedName("FOOD_MENU")
    val foodMenu: String = "1",
    @SerializedName("GNT_NO")
    val gntNo: String = "1",
    @SerializedName("MAP_INDICT_YN")
    val mapIndictYn: String = "1",
    @SerializedName("OWNER_NM")
    val ownerNm: String = "1",
    @SerializedName("RDN_ADDR_CODE")
    val rdnAddrCode: String = "1",
    @SerializedName("RDN_CODE_NM")
    val rdnCodeNm: String = "1",
    @SerializedName("RDN_DETAIL_ADDR")
    val rdnDetailAddr: String = "1",
    @SerializedName("TEL_NO")
    val telNo: String = "1",
    @SerializedName("UPD_TIME")
    val updTime: String = "1",
    @SerializedName("UPSO_NM")
    val upsoNm: String = "1",
    @SerializedName("UPSO_SNO")
    val upsoSno: String = "1",
    @SerializedName("USE_YN")
    val useYn: String = "1",
    @SerializedName("X_CNTS")
    val latitude: String = "1",
    @SerializedName("Y_DNTS")
    val longitude: String = "1"
) : Parcelable