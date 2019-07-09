package com.androidhuman.example.simplegithub.api.model

//[ By room
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
//]
import com.google.gson.annotations.SerializedName

/*
class GithubRepo(
        val name: String,
        @SerializedName("full_name") val fullName: String,
        val owner: GithubOwner,
        // 널 값을 허용할 수 있는 타입으로 선언합니다.
        val description: String?,
        val language: String?,
        @SerializedName("updated_at") val updatedAt: String,
        @SerializedName("stargazers_count") val stars: Int)
// */
//[ By room
@Entity(tableName = "repositories")
class GithubRepo(
        val name: String,
        @SerializedName("full_name")
        @PrimaryKey @ColumnInfo(name = "full_name") val fullName: String,
        @Embedded val owner: GithubOwner,
        // 널 값을 허용할 수 있는 타입으로 선언합니다.
        val description: String?,
        val language: String?,
        @SerializedName("updated_at") @ColumnInfo(name = "updated_at") val updatedAt: String,
        @SerializedName("stargazers_count") val stars: Int)
//]