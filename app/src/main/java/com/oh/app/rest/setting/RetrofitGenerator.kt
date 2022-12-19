package com.oh.app.rest.setting

import com.oh.app.BuildConfig
import com.oh.app.common.*
import com.oh.app.rest.service.KakaoLocalRetrofitService
import com.oh.app.rest.service.MartRetrofitService
import com.oh.app.rest.service.RecipeRetrofitService
import com.oh.app.rest.service.StoreRetrofitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 레트로핏 생성하는 공통 코드
 */
class RetrofitGenerator {
    companion object {
        private var retrofitKakaoService: KakaoLocalRetrofitService? = null
        private var retrofitMartService: MartRetrofitService? = null
        private var retrofitRecipeService: RecipeRetrofitService? = null
        private var retrofitStoreService: StoreRetrofitService? = null

        fun getKakaoInstance(): KakaoLocalRetrofitService {
            if (retrofitKakaoService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(KAKAO_POI_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitKakaoService = retrofit.create(KakaoLocalRetrofitService::class.java)
            }
            return retrofitKakaoService!!
        }

        fun getMartInstance(): MartRetrofitService {
            if (retrofitMartService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(MART_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitMartService = retrofit.create(MartRetrofitService::class.java)
            }
            return retrofitMartService!!
        }

        fun getRecipeInstance(): RecipeRetrofitService {
            if (retrofitRecipeService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(RECIPE_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitRecipeService = retrofit.create(RecipeRetrofitService::class.java)
            }
            return retrofitRecipeService!!
        }


        fun getStoreInstance(): StoreRetrofitService {
            if (retrofitStoreService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(STORE_ADDRESS + BuildConfig.STORE_API_KEY + STORE_LAST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitStoreService = retrofit.create(StoreRetrofitService::class.java)
            }
            return retrofitStoreService!!
        }
    }
}
