package kz.sozdik.core.network

import android.content.SharedPreferences
import android.os.Build
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import kz.sozdik.BuildConfig
import kz.sozdik.core.network.provider.TokenProvider
import kz.sozdik.core.utils.DeviceInfo
import kz.sozdik.core.network.qualifiers.OkHttpInterceptors
import kz.sozdik.core.network.qualifiers.OkHttpNetworkInterceptors
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException

@Module
object NetworkModule {
    private const val API_VERSION = "1.02"

    @Provides
    @JvmStatic
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://sozdik.kz/")
            .build()
    }

    @Provides
    @JvmStatic
    fun provideGson(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Provides
    @JvmStatic
    fun provideTokenProvider(sharedPreferences: SharedPreferences): TokenProvider {
        return TokenProvider(sharedPreferences)
    }

    @Provides
    @JvmStatic
    fun provideOkHttpClient(
        tokenProvider: TokenProvider,
        deviceInfo: DeviceInfo,
        @OkHttpInterceptors interceptors: List<@JvmSuppressWildcards Interceptor>,
        @OkHttpNetworkInterceptors networkInterceptors: List<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.protocols(listOf(Protocol.HTTP_1_1))
        okHttpBuilder.addInterceptor { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            val bodyBuilder =
                FormBody.Builder()
                    .add("api_version", API_VERSION)
                    .add("client_name", BuildConfig.SOZDIK_API_CLIENT_NAME)
                    .add("client_version", BuildConfig.VERSION_NAME)
                    .add("client_password", BuildConfig.SOZDIK_API_KEY)
                    .add("device_id", deviceInfo.deviceId)
                    .add("client_os_version", getClientOsVersion())
            val token = tokenProvider.getToken()
            if (token != null) {
                bodyBuilder.add("auth_token", token)
            }
            val postBodyString = buildString {
                originalRequest.body?.let { requestBody ->
                    append(bodyToString(requestBody))
                }
                append(
                    (if (this.isNotEmpty()) "&" else "") + bodyToString(
                        bodyBuilder.build()
                    )
                )
            }
            val contentType = "application/x-www-form-urlencoded".toMediaType()
            val newRequest = originalRequest.newBuilder()
                .post(postBodyString.toRequestBody(contentType))
                .build()
            chain.proceed(newRequest)
        }
        interceptors.forEach {
            okHttpBuilder.addInterceptor(it)
        }
        networkInterceptors.forEach {
            okHttpBuilder.addInterceptor(it)
        }
        return okHttpBuilder.build()
    }

    private fun bodyToString(request: RequestBody): String? {
        return try {
            val buffer = Buffer()
            request.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            Timber.wtf("Unable to convert RequestBody to String")
            null
        }
    }

    private fun getClientOsVersion(): String {
        var osVersion = Build.VERSION.RELEASE
        if (osVersion.indexOf(".") != osVersion.lastIndexOf(".")) {
            val sb = StringBuilder(osVersion)
            sb.deleteCharAt(osVersion.lastIndexOf("."))
            osVersion = sb.toString()
        }
        return osVersion
    }
}