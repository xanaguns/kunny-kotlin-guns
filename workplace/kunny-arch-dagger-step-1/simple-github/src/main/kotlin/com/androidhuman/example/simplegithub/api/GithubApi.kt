package com.androidhuman.example.simplegithub.api

import com.androidhuman.example.simplegithub.api.model.GithubRepo
import com.androidhuman.example.simplegithub.api.model.RepoSearchResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("search/repositories")
    //By RxJava
    //fun searchRepository(@Query("q") query: String): Call<RepoSearchResponse>
    fun searchRepository(@Query("q") query: String): Observable<RepoSearchResponse>

    @GET("repos/{owner}/{name}")
    fun getRepository(
            @Path("owner") ownerLogin: String,
            //[ By RxJava
            //@Path("name") repoName: String): Call<GithubRepo>
            @Path("name") repoName: String): Observable<GithubRepo>
            //]
}
