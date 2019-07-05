package com.androidhuman.example.simplegithub.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.example.simplegithub.BuildConfig
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.model.GithubAccessToken
import com.androidhuman.example.simplegithub.api.provideAuthApi
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
import com.androidhuman.example.simplegithub.ui.main.MainActivity
import com.androidhuman.example.simplegithub.util.LogMsg
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask

class SignInActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignInActivity"
    }

    // 프로퍼티에 lateinit을 추가합니다.
    internal val api by lazy { provideAuthApi() }

    internal val authTokenProvider by lazy { AuthTokenProvider(this) }

    /*
    internal var accessTokenCall: Call<GithubAccessToken>? = null
    */
    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화합니다.
    // internal var accessTokenCall: Call<GithubAccessToken>? = null 대신 사용합니다.
    internal val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        LogMsg.w(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // View.onClickListener의 본체를 람다 표현식으로 작성합니다.
        btnActivitySignInStart.setOnClickListener {
            // 사용자 인증을 처리하는 URL을 구성합니다.
            // 형식 : https://gitgub.com/login/oauth/authorize?client_id={애플리케이션의 Client ID}
            val authUri = Uri.Builder().scheme("https").authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                    .build()

            // 크롬 커스텀 탭으로 웹 페이지를 표시합니다.
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        // 저장된 액세스 토큰이 있다면 메인 액티비티로 이동합니다.
        if (null != authTokenProvider.token) {
            launchMainActivity()
        }
    }

    override fun onNewIntent(intent: Intent) {
        LogMsg.w(TAG, "onNewIntent()  intent: $intent")
        super.onNewIntent(intent)

        showProgress()

        // 엘비스 연산자를 사용하여 널 값을 검사합니다.
        // intent.data가 널이라면 IllegalArgumentException 예외를 발생시킵니다.
        //
        // 엘비스 연산자를 사용하여 널 값을 검사합니다.
        // intent.data?.getQueryParameter("code") 반환값이 널이라면 IllegalArgumentException 예외를 발생시킵니다.
        val code = intent.data?.getQueryParameter("code")
                ?: throw IllegalStateException("No code exists")
        LogMsg.v(TAG, "code : $code!!")

        getAccessToken(code)
    }

    override fun onStop() {
        super.onStop()
        /*
        accessTokenCall?.run { cancel() }
        */
        // 관리하고 있던 디스포저블 객체를 모두 해제합니다.
        // accessTokenCall?.run { cancel() } 대신 사용합니다.
        disposables.clear()
    }

    private fun getAccessToken(code: String) {
        LogMsg.d(TAG, "getAccessToken()  code: $code")
        /*
        showProgress()

        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성합니다.
        accessTokenCall!!.enqueue(object : Callback<GithubAccessToken> {
            override fun onResponse(call: Call<GithubAccessToken>,
                                    response: Response<GithubAccessToken>) {
                LogMsg.v(TAG, "onResponse()  call: $call, response: $response")
                hideProgress()

                val token = response.body()
                if (response.isSuccessful && null != token) {
                    authTokenProvider.updateToken(token.accessToken)

                    launchMainActivity()
                } else {
                    showError(IllegalStateException(
                            "Not successful: " + response.message()))
                }
            }

            override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
                hideProgress()
                showError(t)
            }
        })
        // */

        // REST API를 통해 액세스 토큰을 요청합니다.
        disposables.add(api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

                // REST API를 통해 받은 응답에서 액세스 토큰만 추출합니다.
                .map { it.accessToken }

                // 이 이후에 수행되는 코드는 모두 메인 스레드에서 실행합니다.
                // RxAndroid에서 제공하는 스케줄러인
                // AndroidSchedulers.mainThread()를 사용합니다.
                .observeOn(AndroidSchedulers.mainThread())

                // 구독할 때 수행할 작업을 구현합니다.
                .doOnSubscribe{ showProgress() }

                // 스트림이 종료될 때 수행할 작업을 구현합니다.
                .doOnTerminate { hideProgress() }

                // 옵서버블을 구독합니다.
                .subscribe({ token ->
                    // API를 통해 액세스 토큰을 정상적으로 받았을 때 처리할 작업을 구현합니다.
                    // 작업 중 오류가 발생하면 이 블록은 호출되지 않습니다.
                    authTokenProvider.updateToken(token)
                    launchMainActivity()
                }) {
                    // 에러블록
                    // 네트워크 오류나 데이터 처리 오류 등
                    // 작업이 정상적으로 완료되지 않았을 때 호출됩니다.
                    showError(it)
                })
    }

    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        //Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
        longToast(throwable.message ?: "No message available")
    }

    private fun launchMainActivity() {
        //startActivity(Intent(
        //        this@SignInActivity, MainActivity::class.java)
        //        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        //        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }
}