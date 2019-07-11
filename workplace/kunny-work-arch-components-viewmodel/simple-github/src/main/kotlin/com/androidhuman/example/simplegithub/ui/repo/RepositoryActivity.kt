package com.androidhuman.example.simplegithub.ui.repo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.provideGithubApi
// 연산자 오버로딩 함수를 import 문에 추가합니다.
import com.androidhuman.example.simplegithub.extensions.plusAssign
//[ By lifecycle
import com.androidhuman.example.simplegithub.rx.AutoClearedDisposable
//]
import com.androidhuman.example.simplegithub.ui.GlideApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_repository.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepositoryActivity : AppCompatActivity() {

    // 정적 필드로 정의되어 있던 항목은 동반 객체 내부에 정의됩니다.
    companion object {

        const val KEY_USER_LOGIN = "user_login"

        const val KEY_REPO_NAME = "repo_name"
    }

    // 프로퍼티에 lateinit을 추가합니다.
    // lazy 프로퍼티로 전환합니다.
    internal val api by lazy { provideGithubApi(this) }

    /*
    // 널 값을 허용하도록 한 후, 초기값을 명시적으로 null로 지정합니다.
    internal var repoCall: Call<GithubRepo>? = null
    // */
    /*
    //[ By RxJava
    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화합니다.
    // internal var repoCall: Call<GithubRepo>? = null 대신 사용합니다.
    internal val disposables = CompositeDisposable()
    //]
    // */
    //[ By lifecycle  CompositeDisposable에서 AutoClearedDisposable로 변경합니다.
    internal val disposables = AutoClearedDisposable(this)
    //]

    internal val dateFormatInResponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())

    internal val dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        //[ By lifecycle  Lifecycle.addObserver() 함수를 사용하여 AutoClearedDisposable 객체를 옵서버로 등록합니다.
        lifecycle += disposables
        //]

        // 엘비스 연산자를 사용하여 널 값을 검사합니다.
        // KEY_USER_LOGIN 이름으로 문자열 값 포함되어 있지 않다면 IllegalArgumentException 예외를 발생시킵니다.
        val login = intent.getStringExtra(KEY_USER_LOGIN)
                ?: throw IllegalArgumentException("No login info exists in extras")

        // 엘비스 연산자를 사용하여 널 값을 검사합니다.
        // KEY_REPO_NAME 이름으로 문자열 값 포함되어 있지 않다면 IllegalArgumentException 예외를 발생시킵니다.
        val repo = intent.getStringExtra(KEY_REPO_NAME)
                ?: throw IllegalArgumentException("No repo info exists in extras")

        showRepositoryInfo(login, repo)
    }

    /* //By lifecycle  onStop() 함수는 더 이상 오버라이드하지 않아도 됩니다.
    override fun onStop() {
        super.onStop()
        /*
        // 액티비티가 화면에서 사라지는 시점에 API 호출 객체가 생성되어 잇다면 API 요청을 취소합니다.
        repoCall?.run { cancel() }
        // */
        //[ By RxJava
        // 관리하고 있던 디스포저블 객체를 모두 해제합니다.
        // repoCall?.run { cancel() } 대신 사용합니다.
        disposables.clear()
        //]
    }
    // */

    private fun showRepositoryInfo(login: String, repoName: String) {
        /*
        showProgress()

        // 이 줄이 실행될 때 repoCall에 반환값이 저장됩니다.
        repoCall = api.getRepository(login, repoName)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성합니다.
        //
        // 앞에서 API 호출에 필요한 객체를 받았으므로, 이 시점에서 repoCall 객체의 값은 널이 아닙니다.
        // 따라서 비 널 값 보증(!!)을 사용하여 이 객체를 사용합니다.
        repoCall!!.enqueue(object : Callback<GithubRepo> {
            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                hideProgress(true)

                val repo = response.body()
                if (response.isSuccessful && null != repo) {
                    GlideApp.with(this@RepositoryActivity)
                            .load(repo.owner.avatarUrl)
                            .into(ivActivityRepositoryProfile)

                    tvActivityRepositoryName.text = repo.fullName
                    tvActivityRepositoryStars.text = resources
                            .getQuantityString(R.plurals.star, repo.stars, repo.stars)
                    if (null == repo.description) {
                        tvActivityRepositoryDescription.setText(R.string.no_description_provided)
                    } else {
                        tvActivityRepositoryDescription.text = repo.description
                    }
                    if (null == repo.language) {
                        tvActivityRepositoryLanguage.setText(R.string.no_language_specified)
                    } else {
                        tvActivityRepositoryLanguage.text = repo.language
                    }

                    try {
                        val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
                        tvActivityRepositoryLastUpdate.text = dateFormatToShow.format(lastUpdate)
                    } catch (e: ParseException) {
                        tvActivityRepositoryLastUpdate.text = getString(R.string.unknown)
                    }

                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<GithubRepo>, t: Throwable) {
                hideProgress(false)
                // showError() 함수는 널 값을 허용하지 않으나 t.message는 널 값을 반환할 수 있습니다.
                showError(t.message)
            }
        })
        // */

        //[ By RxJava
        // REST API를 통해 저장소 정보를 요청합니다.
        // '+=' 연산자로 디스포저블을 CompositeDisposable에 추가합니다.
        //disposables.add(api.getRepository(login, repoName)
        disposables += api.getRepository(login, repoName)

                // 이 이후에 수행되는 코드는 모두 메인 스레드에서 실행합니다.
                .observeOn(AndroidSchedulers.mainThread())

                // 구독할 때 수행할 작업을 구현합니다.
                .doOnSubscribe { showProgress() }

                // 에러가 발생했을 때 수행할 작업을 구현합니다.
                .doOnError { hideProgress(false) }

                // 스트림이 정상 종료되었을 때 수행할 작업을 구현합니다.
                .doOnComplete { hideProgress(true) }

                // 옵서버블을 구독합니다.
                .subscribe({ repo ->

                    // API를 통해 저장소 정보를 정상적으로 받았을 때 처리할 작업을 구현합니다.
                    // 작업 중 오류가 발생하면 이 블록은 호출되지 않습니다.
                    GlideApp.with(this@RepositoryActivity)
                            .load(repo.owner.avatarUrl)
                            .into(ivActivityRepositoryProfile)

                    tvActivityRepositoryName.text = repo.fullName
                    tvActivityRepositoryStars.text = resources
                            .getQuantityString(R.plurals.star, repo.stars, repo.stars)
                    if (null == repo.description) {
                        tvActivityRepositoryDescription.setText(R.string.no_description_provided)
                    } else {
                        tvActivityRepositoryDescription.text = repo.description
                    }
                    if (null == repo.language) {
                        tvActivityRepositoryLanguage.setText(R.string.no_language_specified)
                    } else {
                        tvActivityRepositoryLanguage.text = repo.language
                    }

                    try {
                        val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
                        tvActivityRepositoryLastUpdate.text = dateFormatToShow.format(lastUpdate)
                    } catch (e: ParseException) {
                        tvActivityRepositoryLastUpdate.text = getString(R.string.unknown)
                    }
                }) {
                    // 에러블록
                    // 네트워크 오류나 데이터 처리 오류 등
                    // 작업이 정상적으로 완료되지 않았을 때 호출됩니다.
                    showError(it.message)
                }//)
        //]
    }

    private fun showProgress() {
        llActivityRepositoryContent.visibility = View.GONE
        pbActivityRepository.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        llActivityRepositoryContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pbActivityRepository.visibility = View.GONE
    }

    private fun showError(message: String?) {
        // with() 함수를 사용하여 tvActivityRepositoryMessage 범위 내에서 작업을 수행합니다.
        with(tvActivityRepositoryMessage) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }
}
