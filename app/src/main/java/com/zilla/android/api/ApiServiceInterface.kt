package com.zilla.android.api

import com.zilla.android.models.Category
import com.zilla.android.models.Song
import com.zilla.android.util.Constants
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


interface ApiServiceInterface {

    @GET("categories/")
    fun getCategories(): Observable<List<Category>>



    @GET("categories/{id}/?page=1&count=10")
    fun getSongByPlayList(@Path("id") id: String): Observable<List<Song>>


    companion object Factory {
        fun create(): ApiServiceInterface {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl(Constants.BASE_URL)
                    .build()
            return retrofit.create(ApiServiceInterface::class.java)
        }
    }
}