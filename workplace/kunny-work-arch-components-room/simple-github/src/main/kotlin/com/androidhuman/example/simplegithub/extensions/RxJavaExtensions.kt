package com.androidhuman.example.simplegithub.extensions

import io.reactivex.disposables.CompositeDisposable
import com.androidhuman.example.simplegithub.rx.AutoClearedDisposable
import io.reactivex.Completable  //By room
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers  //By room

/*
operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}
// */
//[ By lifecycle  CompositeDisposable.plusAssign() 대신 아래 함수를 추가합니다.
operator fun AutoClearedDisposable.plusAssign(disposable: Disposable)
        = this.add(disposable)
//]

//[ By room
fun runOnIoScheduler(func: () -> Unit): Disposable
        = Completable.fromCallable(func).subscribeOn(Schedulers.io()).subscribe()
//]