package com.oh.app.data.kakao.local

import com.google.gson.annotations.SerializedName

data class CurrentLocation(
    @SerializedName("documents")
    val documents: List<Document>,
    @SerializedName("meta")
    val meta: Meta
)

data class Document(
    @SerializedName("address")
    val address: Address,
    @SerializedName("road_address")
    val road_address: RoadAddress
)

data class Address(
    @SerializedName("address_name")
    val address_name: String,
    @SerializedName("main_address_no")
    val main_address_no: String,
    @SerializedName("mountain_yn")
    val mountain_yn: String,
    @SerializedName("region_1depth_name")
    val region_1depth_name: String,
    @SerializedName("region_2depth_name")
    val region_2depth_name: String,
    @SerializedName("region_3depth_name")
    val region_3depth_name: String,
    @SerializedName("sub_address_no")
    val sub_address_no: String,
    @SerializedName("zip_code")
    val zip_code: String
)

data class Meta(
    @SerializedName("total_count")
    val total_count: Int
)

data class RoadAddress(
    @SerializedName("address_name")
    val address_name: String,
    @SerializedName("building_name")
    val building_name: String,
    @SerializedName("main_building_no")
    val main_building_no: String,
    @SerializedName("region_1depth_name")
    val region_1depth_name: String,
    @SerializedName("region_2depth_name")
    val region_2depth_name: String,
    @SerializedName("region_3depth_name")
    val region_3depth_name: String,
    @SerializedName("road_name")
    val road_name: String,
    @SerializedName("sub_building_no")
    val sub_building_no: String,
    @SerializedName("underground_yn")
    val underground_yn: String,
    @SerializedName("zone_no")
    val zone_no: String
)