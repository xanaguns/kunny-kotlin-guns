package com.androidhuman.example.simplegithub.di

import android.arch.persistence.room.Room
import android.content.Context
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
import com.androidhuman.example.simplegithub.data.SearchHistoryDao
import com.androidhuman.example.simplegithub.data.SimpleGithubDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

//[ ++ By dagger_1
@Module
class LocalDataModule {

    //====================================================================================
    // SharedPreferences
    //====================================================================================
    // 인증 토큰을 관리하는 객체인 AuthTokenProvider를 제공합니다.
    // AuthTokenProvider는 SharedPreferences를 기반으로 인증 토큰을 관리합니다.
    // "appContext"라는 이름으로 구분되는 Context 객체를 필요로 합니다.
    @Provides
    @Singleton
    fun provideAuthTokenProvider(@Named("appContext") context: Context): AuthTokenProvider
            = AuthTokenProvider(context)


    //[ ++ By room
    //====================================================================================
    // Room Database
    //====================================================================================
    // 저장소 조회 기록을 관리하는 데이터 접근 객체인 SearchHistoryDao를 제공합니다.
    @Provides
    @Singleton
    fun provideSearchHistoryDao(db: SimpleGithubDatabase): SearchHistoryDao
            = db.searchHistoryDao()

    // 데이터베이스를 관리하는 객체인 SimpleGithubDatabase 룸 데이터베이스를 제공합니다.
    // "appContext"라는 이름으로 구분되는 Context 객체를 필요로 합니다.
    @Provides
    @Singleton
    fun provideDatabase(@Named("appContext") context: Context): SimpleGithubDatabase
            = Room.databaseBuilder(context,
            SimpleGithubDatabase::class.java, "simple_github.db")
            .build()
    //] -- By room
}
//] -- By dagger_1
