package com.androidhuman.example.simplegithub.api

import android.content.Context
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

//[ ++ By dagger_1
/*
// AuthApi 객체를 제공합니다.
fun provideAuthApi(): AuthApi
        = Retrofit.Builder()
        .baseUrl("https://github.com/")

        // OkHttpClient 객체를 인자로 받습니다.
        // HTTP 요청에 인증 토큰을 추가하지 않는 OkHttpClient 객체를 사용합니다.
        .client(provideOkHttpClient(provideLoggingInterceptor(), null))

        // CallAdapter.Factory 객체를 인자로 받습니다.
        //[ By RxJava
        // 받은 응답을 옵서버블 형태로 변환해주도록 합니다.
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        //]

        // Converter.Factory 객체를 인자로 받습니다.
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

// GithubApi 객체를 제공합니다.
fun provideGithubApi(context: Context): GithubApi
        = Retrofit.Builder()
        .baseUrl("https://api.github.com/")

        // OkHttpClient 객체를 인자로 받습니다.
        // HTTP 요청에 인증 토큰을 추가하는 OkHttpClient 객체를 사용합니다.
        .client(provideOkHttpClient(provideLoggingInterceptor(),
                provideAuthInterceptor(provideAuthTokenProvider(context))))

        // CallAdapter.Factory 객체를 인자로 받습니다.
        //[ By RxJava
        // 받은 응답을 옵서버블 형태로 변환하며, 비동기 방식으로 API를 호출합니다.
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        //]

        // Converter.Factory 객체를 인자로 받습니다.
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GithubApi::class.java)

// OkHttpClient 객체를 제공합니다.
// HTTP 요청과 응답을 로그로 출력해주는 HttpLoggingInterceptor는 필수이지만,
// HTTP 요청에 인증 토큰을 추가해주는 AuthInterceptor는 선택적으로 받습니다.
private fun provideOkHttpClient(
        interceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor?): OkHttpClient
        // run() 함수로 OkHttpClient.Builder() 변수 선언을 제거합니다.
        = OkHttpClient.Builder()
        .run {
            if (null != authInterceptor) {
                addInterceptor(authInterceptor)
            }
            addInterceptor(interceptor)
            build()
        }

// apply() 함수로 인스턴스 생성과 프로퍼티 값 변경을 동시에 수행합니다.
private fun provideLoggingInterceptor(): HttpLoggingInterceptor
        = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

private fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
    val token = provider.token ?: throw IllegalStateException("authToken cannot be null.")
    return AuthInterceptor(token)
}

private fun provideAuthTokenProvider(context: Context): AuthTokenProvider
        = AuthTokenProvider(context.applicationContext)

internal class AuthInterceptor(private val token: String) : Interceptor {

    // with() 함수와 run() 함수로 추가 변수 선언을 제거합니다.
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder().run {
            addHeader("Authorization", "token $token")
            build()
        }
        proceed(newRequest)
    }

}
// */
//] -- By dagger_1