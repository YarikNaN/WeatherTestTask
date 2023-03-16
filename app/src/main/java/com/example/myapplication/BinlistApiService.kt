package com.example.myapplication

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class UnsafeOkHttpClient {
    companion object {
        fun getUnsafeOkHttpClient(): OkHttpClient {
            try {
                val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    }

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                })
                val sslContext: SSLContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
                return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}


interface BinListApiService {

    @Headers("Accept-Version: 3")
    @GET("{bin}")
    fun getBinInfo(@Path("bin") bin: String): Call<BinInfo>
}
data class BinInfo(
    val number: Number?,
    val scheme: String?,
    val type: String?,
    val brand: String?,
    val prepaid: Boolean?,
    val country: Country?,
    val bank: Bank?
) {
    override fun toString(): String {
        return listOf(
            "Number: ${number ?: "N/A"}",
            "Scheme: ${scheme ?: "N/A"}",
            "Type: ${type ?: "N/A"}",
            "Brand: ${brand ?: "N/A"}",
            "Prepaid: ${prepaid ?: "N/A"}",
            "Country: ${country?.name ?: "N/A"}",
            "Bank: ${bank?.name ?: "N/A"}"
        ).joinToString(separator = "\n")
    }
}

data class Number(
    val length: Int?,
    val luhn: Boolean?
)


data class Country(
    val numeric: String?,
    val alpha2: String?,
    val name: String?,
    val emoji: String?,
    val currency: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class Bank(
    val name: String?,
    val url: String?,
    val phone: String?,
    val city: String?
)



object BinListApiClient {
    private const val BASE_URL = "https://lookup.binlist.net/"
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
        .build()

    val apiService = retrofit.create(BinListApiService::class.java)
}


