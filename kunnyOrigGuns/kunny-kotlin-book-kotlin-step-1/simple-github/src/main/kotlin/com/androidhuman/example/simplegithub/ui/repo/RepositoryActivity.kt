package com.androidhuman.example.simplegithub.ui.repo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.GithubApi
import com.androidhuman.example.simplegithub.api.GithubApiProvider
import com.androidhuman.example.simplegithub.api.model.GithubRepo
import com.androidhuman.example.simplegithub.ui.GlideApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class RepositoryActivity : AppCompatActivity() {

    // 프로퍼티에 lateinit을 추가합니다.
    internal lateinit var llContent: LinearLayout

    internal lateinit var ivProfile: ImageView

    internal lateinit var tvName: TextView

    internal lateinit var tvStars: TextView

    internal lateinit var tvDescription: TextView

    internal lateinit var tvLanguage: TextView

    internal lateinit var tvLastUpdate: TextView

    internal lateinit var pbProgress: ProgressBar

    internal lateinit var tvMessage: TextView

    internal lateinit var api: GithubApi

    internal lateinit var repoCall: Call<GithubRepo>

    internal var dateFormatInResponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())

    internal var dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        llContent = findViewById(R.id.llActivityRepositoryContent)
        ivProfile = findViewById(R.id.ivActivityRepositoryProfile)
        tvName = findViewById(R.id.tvActivityRepositoryName)
        tvStars = findViewById(R.id.tvActivityRepositoryStars)
        tvDescription = findViewById(R.id.tvActivityRepositoryDescription)
        tvLanguage = findViewById(R.id.tvActivityRepositoryLanguage)
        tvLastUpdate = findViewById(R.id.tvActivityRepositoryLastUpdate)
        pbProgress = findViewById(R.id.pbActivityRepository)
        tvMessage = findViewById(R.id.tvActivityRepositoryMessage)

        api = GithubApiProvider.provideGithubApi(this)

        // 엘비스 연산자를 사용하여 널 값을 검사합니다.
        // KEY_USER_LOGIN 이름으로 문자열 값 포함되어 있지 않다면 IllegalArgumentException 예외를 발생시킵니다.
        val login = intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalArgumentException(
                "No login info exists in extras")

        // 엘비스 연산자를 사용하여 널 값을 검사합니다.
        // KEY_REPO_NAME 이름으로 문자열 값 포함되어 있지 않다면 IllegalArgumentException 예외를 발생시킵니다.
        val repo = intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalArgumentException(
                "No repo info exists in extras")

        showRepositoryInfo(login, repo)
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        showProgress()

        repoCall = api.getRepository(login, repoName)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성합니다.
        repoCall.enqueue(object : Callback<GithubRepo> {
            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                hideProgress(true)

                val repo = response.body()
                if (response.isSuccessful && null != repo) {
                    GlideApp.with(this@RepositoryActivity)
                            .load(repo.owner.avatarUrl)
                            .into(ivProfile)

                    tvName.text = repo.fullName
                    tvStars.text = resources
                            .getQuantityString(R.plurals.star, repo.stars, repo.stars)
                    if (null == repo.description) {
                        tvDescription.setText(R.string.no_description_provided)
                    } else {
                        tvDescription.text = repo.description
                    }
                    if (null == repo.language) {
                        tvLanguage.setText(R.string.no_language_specified)
                    } else {
                        tvLanguage.text = repo.language
                    }

                    try {
                        val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
                        tvLastUpdate.text = dateFormatToShow.format(lastUpdate)
                    } catch (e: ParseException) {
                        tvLastUpdate.text = getString(R.string.unknown)
                    }

                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<GithubRepo>, t: Throwable) {
                hideProgress(false)
                showError(t.message)
            }
        })
    }

    private fun showProgress() {
        llContent.visibility = View.GONE
        pbProgress.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        llContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pbProgress.visibility = View.GONE
    }

    private fun showError(message: String?) {
        // message가 널 값인 경우 "Unexpected error." 메시지를 표시합니다.
        tvMessage.text = message ?: "Unexpected error."
        tvMessage.visibility = View.VISIBLE
    }

    // 정적 필드로 정의되어 있던 항목은 동반 객체 내부에 정의됩니다.
    companion object {

        val KEY_USER_LOGIN = "user_login"

        val KEY_REPO_NAME = "repo_name"
    }
}
