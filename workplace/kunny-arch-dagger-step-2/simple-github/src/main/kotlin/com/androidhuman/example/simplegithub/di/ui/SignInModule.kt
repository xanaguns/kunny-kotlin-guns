package com.androidhuman.example.simplegithub.di.ui

import com.androidhuman.example.simplegithub.api.AuthApi
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
import com.androidhuman.example.simplegithub.ui.signin.SignInViewModelFactory
import dagger.Module
import dagger.Provides

//[ ++ By dagger_2
// 모듈 클래스로 표시합니다.
@Module
class SignInModule {

    // SignInViewModelFactory 객체를 제공합니다.
    @Provides
    fun provideViewModelFactory(authApi: AuthApi, authTokenProvider: AuthTokenProvider)
            : SignInViewModelFactory
            = SignInViewModelFactory(authApi, authTokenProvider)
}
//] -- By dagger_2
