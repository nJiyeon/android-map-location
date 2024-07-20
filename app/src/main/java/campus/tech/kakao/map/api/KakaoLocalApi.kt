package campus.tech.kakao.map.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApi {
    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15
    ): KakaoSearchResponse
}

// 데이터 모델
data class KakaoSearchResponse(
    @SerializedName("documents")
    val documents: List<Document>,

    @SerializedName("meta")
    val meta: Meta
)

data class Document(
    @SerializedName("place_name")
    val placeName: String,

    @SerializedName("address_name")
    val addressName: String,

    @SerializedName("category_group_name")
    val categoryGroupName: String,

    @SerializedName("y")
    val latitude: Double,

    @SerializedName("x")
    val longitude: Double

)

data class Meta(
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("pageable_count")
    val pageableCount: Int,

    @SerializedName("is_end")
    val isEnd: Boolean
)
