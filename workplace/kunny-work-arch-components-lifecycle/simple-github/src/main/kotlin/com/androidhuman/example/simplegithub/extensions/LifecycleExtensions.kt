package com.androidhuman.example.simplegithub.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver

//[ By lifecycle
operator fun Lifecycle.plusAssign(observer: LifecycleObserver)
        = this.addObserver(observer)
//]