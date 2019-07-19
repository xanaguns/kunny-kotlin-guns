package com.androidhuman.example.simplegithub.di

import com.androidhuman.example.simplegithub.di.ui.MainModule
import com.androidhuman.example.simplegithub.di.ui.RepositoryModule
import com.androidhuman.example.simplegithub.di.ui.SearchModule
import com.androidhuman.example.simplegithub.di.ui.SignInModule
import com.androidhuman.example.simplegithub.ui.main.MainActivity
import com.androidhuman.example.simplegithub.ui.repo.RepositoryActivity
import com.androidhuman.example.simplegithub.ui.search.SearchActivity
import com.androidhuman.example.simplegithub.ui.signin.SignInActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

//[ ++ By dagger_1
@Module
abstract class ActivityBinder {

    // SignInActivity를 객체 그래프에 추가할 수 있도록 합니다.
    // SignInModule을 객체 그래프에 추가합니다.
    @ContributesAndroidInjector(modules = arrayOf(SignInModule::class))  //By dagger_2
    abstract fun bindSignInActivity(): SignInActivity

    // MainActivity를 객체 그래프에 추가할 수 있도록 합니다.
    // MainModule을 객체 그래프에 추가합니다.
    @ContributesAndroidInjector(modules = arrayOf(MainModule::class))  //By dagger_2
    abstract fun bindMainActivity(): MainActivity

    // SearchActivity를 객체 그래프에 추가할 수 있도록 합니다.
    // SearchModule을 객체 그래프에 추가합니다.
    @ContributesAndroidInjector(modules = arrayOf(SearchModule::class))  //By dagger_2
    abstract fun bindSearchActivity(): SearchActivity

    // RepositoryActivity를 객체 그래프에 추가할 수 있도록 합니다.
    // RepositoryModule을 객체 그래프에 추가합니다.
    @ContributesAndroidInjector(modules = arrayOf(RepositoryModule::class))  //By dagger_2
    abstract fun bindRepositoryActivity(): RepositoryActivity
}
//] -- By dagger_1
