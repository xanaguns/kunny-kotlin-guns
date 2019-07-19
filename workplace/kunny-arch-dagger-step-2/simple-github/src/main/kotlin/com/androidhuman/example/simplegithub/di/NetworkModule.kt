package com.androidhuman.example.simplegithub.di

import com.androidhuman.example.simplegithub.api.AuthInterceptor
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

//[ ++ By dagger_1
// 모듈 클래스로 표시합니다.
@Module
class NetworkModule {

    // "unauthorized"라는 이름으로 구분할 수 있는 OkHttpClient 객체를 제공합니다.
    // 여기에서 제공하는 OkHttpClient 객체는 요청에 인증 토큰을 추가하지 않습니다.
    @Provides
    @Named("unauthorized")
    @Singleton
    fun provideUnauthorizedOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient

            = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    // "authorized"라는 이름으로 구분할 수 있는 OkHttpClient 객체를 제공합니다.
    // 여기에서 제공하는 OkHttpClient 객체는 요청에 인증 토큰을 추가해줍니다.
    @Provides
    @Named("authorized")
    @Singleton
    fun provideAuthorizedOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            authInterceptor: AuthInterceptor): OkHttpClient

            = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    // HttpLoggingInterceptor 객체를 제공합니다.
    // apply() 함수로 인스턴스 생성과 프로퍼티 값 변경을 동시에 수행합니다.
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor
            = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    // AuthInterceptor 객체를 제공합니다.
    @Provides
    @Singleton
    fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
        val token = provider.token ?: throw IllegalStateException("authToken cannot be null")
        return AuthInterceptor(token)
    }
}
//] -- By dagger_1
