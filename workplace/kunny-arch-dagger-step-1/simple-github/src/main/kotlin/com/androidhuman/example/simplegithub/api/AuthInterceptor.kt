package com.androidhuman.example.simplegithub.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

//[ ++ By dagger_1
class AuthInterceptor(private val token: String) : Interceptor {

    // with() 함수와 run() 함수로 추가 변수 선언을 제거합니다.
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder().run {
            addHeader("Authorization", "token " + token)
            build()
        }
        proceed(newRequest)
    }
}
//] -- By dagger_1
