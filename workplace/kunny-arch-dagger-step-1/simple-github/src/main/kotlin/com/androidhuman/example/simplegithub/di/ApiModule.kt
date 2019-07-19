package com.androidhuman.example.simplegithub.di

import com.androidhuman.example.simplegithub.api.AuthApi
import com.androidhuman.example.simplegithub.api.GithubApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

//[ ++ By dagger_1
// 모듈 클래스로 표시합니다.
@Module
class ApiModule {

    // AuthApi 객체를 제공합니다.
    // 이 객체를 생성할 때 필요한 객체들은 함수의 인자로 선언합니다.
    @Provides
    @Singleton
    fun provideAuthApi(
            // 인증 토큰을 추가하지 않는 OkHttpClient 객체를 "unauthorized"라는 이름으로 구분합니다.
            @Named("unauthorized") okHttpClient: OkHttpClient,
            callAdapter: CallAdapter.Factory,
            converter: Converter.Factory): AuthApi

            = Retrofit.Builder()
            .baseUrl("https://github.com")

            // OkHttpClient 객체를 인자로 받습니다.
            // HTTP 요청에 인증 토큰을 추가하지 않는 OkHttpClient 객체를 사용합니다.
            .client(okHttpClient)

            // CallAdapter.Factory 객체를 인자로 받습니다.
            //[ By RxJava
            // 받은 응답을 옵서버블 형태로 변환해주도록 합니다.
            .addCallAdapterFactory(callAdapter)
            //]

            // Converter.Factory 객체를 인자로 받습니다.
            .addConverterFactory(converter)
            .build()
            .create(AuthApi::class.java)

    // GithubApi 객체를 제공합니다.
    // 이 객체를 생성할 때 필요한 객체들은 함수의 인자로 선언합니다.
    @Provides
    @Singleton
    fun provideGithubApi(
            // 인증 토큰을 추가하는 OkHttpClient 객체를 "authorized"라는 이름으로 구분합니다.
            @Named("authorized") okHttpClient: OkHttpClient,
            callAdapter: CallAdapter.Factory,
            converter: Converter.Factory): GithubApi

            = Retrofit.Builder()
            .baseUrl("https://api.github.com")

            // OkHttpClient 객체를 인자로 받습니다.
            // HTTP 요청에 인증 토큰을 추가하는 OkHttpClient 객체를 사용합니다.
            .client(okHttpClient)

            // CallAdapter.Factory 객체를 인자로 받습니다.
            //[ By RxJava
            // 받은 응답을 옵서버블 형태로 변환하며, 비동기 방식으로 API를 호출합니다.
            .addCallAdapterFactory(callAdapter)
            //]

            // Converter.Factory 객체를 인자로 받습니다.
            .addConverterFactory(converter)
            .build()
            .create(GithubApi::class.java)

    // CallAdapter.Factory 객체를 제공합니다.
    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory
            = RxJava2CallAdapterFactory.createAsync()

    // Converter.Factory 객체를 제공합니다.
    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory
            = GsonConverterFactory.create()
}
//] -- By dagger_1