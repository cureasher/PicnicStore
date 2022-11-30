import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeInfoData(
    @SerializedName("COOKRCP01")
    val COOKRCP01: COOKRCP01
) : Parcelable

@Parcelize
data class COOKRCP01(
    @SerializedName("RESULT")
    val RESULT: RESULT = RESULT(),
    @SerializedName("row")
    val row: ArrayList<Row> = arrayListOf(),
    @SerializedName("total_count")
    val total_count: String = ""
) : Parcelable

@Parcelize
data class RESULT(
    @SerializedName("CODE")
    val CODE: String = "",
    @SerializedName("MSG")
    val MSG: String = ""
) : Parcelable

@Parcelize
data class Row(
    @SerializedName("ATT_FILE_NO_MAIN")
    val ATT_FILE_NO_MAIN: String = "",
    @SerializedName("ATT_FILE_NO_MK")
    val ATT_FILE_NO_MK: String = "",
    @SerializedName("HASH_TAG")
    val HASH_TAG: String = "",
    @SerializedName("INFO_CAR")
    val INFO_CAR: String = "",
    @SerializedName("INFO_ENG")
    val INFO_ENG: String = "",
    @SerializedName("INFO_FAT")
    val INFO_FAT: String = "",
    @SerializedName("INFO_NA")
    val INFO_NA: String = "",
    @SerializedName("INFO_PRO")
    val INFO_PRO: String = "",
    @SerializedName("INFO_WGT")
    val INFO_WGT: String = "",
    @SerializedName("MANUAL01")
    val MANUAL01: String = "",
    @SerializedName("MANUAL02")
    val MANUAL02: String = "",
    @SerializedName("MANUAL03")
    val MANUAL03: String = "",
    @SerializedName("MANUAL04")
    val MANUAL04: String = "",
    @SerializedName("MANUAL05")
    val MANUAL05: String = "",
    @SerializedName("MANUAL06")
    val MANUAL06: String = "",
    @SerializedName("MANUAL07")
    val MANUAL07: String = "",
    @SerializedName("MANUAL08")
    val MANUAL08: String = "",
    @SerializedName("MANUAL09")
    val MANUAL09: String = "",
    @SerializedName("MANUAL10")
    val MANUAL10: String = "",
    @SerializedName("MANUAL11")
    val MANUAL11: String = "",
    @SerializedName("MANUAL12")
    val MANUAL12: String = "",
    @SerializedName("MANUAL13")
    val MANUAL13: String = "",
    @SerializedName("MANUAL14")
    val MANUAL14: String = "",
    @SerializedName("MANUAL15")
    val MANUAL15: String = "",
    @SerializedName("MANUAL16")
    val MANUAL16: String = "",
    @SerializedName("MANUAL17")
    val MANUAL17: String = "",
    @SerializedName("MANUAL18")
    val MANUAL18: String = "",
    @SerializedName("MANUAL19")
    val MANUAL19: String = "",
    @SerializedName("MANUAL20")
    val MANUAL20: String = "",
    @SerializedName("MANUAL_IMG01")
    val MANUAL_IMG01: String = "",
    @SerializedName("MANUAL_IMG02")
    val MANUAL_IMG02: String = "",
    @SerializedName("MANUAL_IMG03")
    val MANUAL_IMG03: String = "",
    @SerializedName("MANUAL_IMG04")
    val MANUAL_IMG04: String = "",
    @SerializedName("MANUAL_IMG05")
    val MANUAL_IMG05: String = "",
    @SerializedName("MANUAL_IMG06")
    val MANUAL_IMG06: String = "",
    @SerializedName("MANUAL_IMG07")
    val MANUAL_IMG07: String = "",
    @SerializedName("MANUAL_IMG08")
    val MANUAL_IMG08: String = "",
    @SerializedName("MANUAL_IMG09")
    val MANUAL_IMG09: String = "",
    @SerializedName("MANUAL_IMG10")
    val MANUAL_IMG10: String = "",
    @SerializedName("MANUAL_IMG11")
    val MANUAL_IMG11: String = "",
    @SerializedName("MANUAL_IMG12")
    val MANUAL_IMG12: String = "",
    @SerializedName("MANUAL_IMG13")
    val MANUAL_IMG13: String = "",
    @SerializedName("MANUAL_IMG14")
    val MANUAL_IMG14: String = "",
    @SerializedName("MANUAL_IMG15")
    val MANUAL_IMG15: String = "",
    @SerializedName("MANUAL_IMG16")
    val MANUAL_IMG16: String = "",
    @SerializedName("MANUAL_IMG17")
    val MANUAL_IMG17: String = "",
    @SerializedName("MANUAL_IMG18")
    val MANUAL_IMG18: String = "",
    @SerializedName("MANUAL_IMG19")
    val MANUAL_IMG19: String = "",
    @SerializedName("MANUAL_IMG20")
    val MANUAL_IMG20: String = "",
    @SerializedName("RCP_NM")
    val RCP_NM: String = "",
    @SerializedName("RCP_PARTS_DTLS")
    val RCP_PARTS_DTLS: String = "",
    @SerializedName("RCP_PAT2")
    val RCP_PAT2: String = "",
    @SerializedName("RCP_SEQ")
    val RCP_SEQ: String = "",
    @SerializedName("RCP_WAY2")
    val RCP_WAY2: String = ""
) : Parcelable