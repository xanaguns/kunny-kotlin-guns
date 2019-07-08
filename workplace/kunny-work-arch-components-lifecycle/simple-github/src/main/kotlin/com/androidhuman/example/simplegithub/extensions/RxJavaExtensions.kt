package com.androidhuman.example.simplegithub.extensions

import io.reactivex.disposables.CompositeDisposable
import com.androidhuman.example.simplegithub.rx.AutoClearedDisposable
import io.reactivex.disposables.Disposable

/*
operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}
// */
//[ By lifecycle
operator fun AutoClearedDisposable.plusAssign(disposable: Disposable)
        = this.add(disposable)
//]