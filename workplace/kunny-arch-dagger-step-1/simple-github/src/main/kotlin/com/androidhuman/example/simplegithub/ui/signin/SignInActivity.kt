package com.androidhuman.example.simplegithub.ui.signin

//[ By viewmodel
import android.arch.lifecycle.ViewModelProviders
//]
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.example.simplegithub.BuildConfig
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.AuthApi
//import com.androidhuman.example.simplegithub.api.provideAuthApi
import com.androidhuman.example.simplegithub.data.AuthTokenProvider
// 연산자 오버로딩 함수를 import 문에 추가합니다.
import com.androidhuman.example.simplegithub.extensions.plusAssign
//[ By lifecycle
import com.androidhuman.example.simplegithub.rx.AutoClearedDisposable
//]
import com.androidhuman.example.simplegithub.ui.main.MainActivity
import com.androidhuman.example.simplegithub.util.LogMsg
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_in.*
// 사용하는 함수를 import 문에 추가합니다.
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask
import javax.inject.Inject

// AppCompatActivity 대신 DaggerAppCompatActivity를 상속합니다.
class SignInActivity : DaggerAppCompatActivity() {

    companion object {
        const val TAG = "SignInActivity"
    }

    /*
    // 프로퍼티에 lateinit을 추가합니다.
    // Lazy 프로퍼티를 사용하기 위해 변수(var)에서 값(val)로 바꾼 후 사용합니다.
    // 타입 선언을 생략합니다.
    internal val api by lazy { provideAuthApi() }

    internal val authTokenProvider by lazy { AuthTokenProvider(this) }
    // */

    /*
    // 널 값을 허용하도록 한 후, 초기값을 명시적으로 null로 지정합니다.
    internal var accessTokenCall: Call<GithubAccessToken>? = null
    // */
    /*
    //[ By RxJava
    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화합니다.
    // internal var accessTokenCall: Call<GithubAccessToken>? = null 대신 사용합니다.
    internal val disposables = CompositeDisposable()
    //]
    // */
    //[ By lifecycle  CompositeDisposable에서 AutoClearedDisposable로 변경합니다.
    internal val disposables = AutoClearedDisposable(this)
    //]

    //[ ++ By viewmodel
    // 액티비티가 완전히 종료되기 전까지 이벤트를 계속 받기 위해 추가합니다.
    internal val viewDisposables
            = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    // SignInViewModel을 생성할 때 필요한 뷰모델 팩토리 클래스의 인스턴스를 생성합니다.
    internal val viewModelFactory by lazy {
        //SignInViewModelFactory(provideAuthApi(), AuthTokenProvider(this))
        //[ By dagger_1
        // 대거를 통해 주입받은 객체를 생성자의 인자로 전달합니다.
        SignInViewModelFactory(authApi, authTokenProvider)
        //]
    }

    // 뷰모델의 인스턴스는 onCreate()에서 받으므로, lateinit으로 선언합니다.
    lateinit var viewModel: SignInViewModel
    //] -- By viewmodel

    //[ ++ By dagger_1
    // 대거를 통해 AuthApi 객체를 주입받는 프로퍼티를 선언합니다.
    // @Inject 어노테이션을 추가해야 대거로부터 객체를 주입받을 수 있습니다.
    // 선언 시점에 프로퍼티를 초기화할 수 없으므로 lateinit var로 선언합니다.
    @Inject
    lateinit var authApi: AuthApi

    // 대거를 통해 AuthTokenProvider 객체를 주입받는 프로퍼티를 선언합니다.
    @Inject
    lateinit var authTokenProvider: AuthTokenProvider
    //] -- By dagger_1

    override fun onCreate(savedInstanceState: Bundle?) {
        LogMsg.w(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //[ By viewmodel
        // SignInViewModel의 인스턴스를 받습니다.
        viewModel = ViewModelProviders.of(
                this, viewModelFactory)[SignInViewModel::class.java]
        //]

        //[ By lifecycle  Lifecycle.addObserver() 함수를 사용하여 AutoClearedDisposable 객체를 옵서버로 등록합니다.
        lifecycle += disposables
        //]
        //[ By viewmodel
        // viewDisposables에서 이 액티비티의 생명주기 이벤트를 받도록 합니다.
        lifecycle += viewDisposables
        //]

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

        /*
        // 저장된 액세스 토큰이 있다면 메인 액티비티로 이동합니다.
        if (null != authTokenProvider.token) {
            launchMainActivity()
        }
        // */
        //[ ++ By viewmodel
        // 액세스 토큰 이벤트를 구독합니다.
        viewDisposables += viewModel.accessToken
                // 액세스 토큰이 없는 경우는 무시합니다.
                .filter { !it.isEmpty }
                .observeOn(AndroidSchedulers.mainThread())
                // 액세스 토큰이 있는 것을 확인했다면 메인 화면으로 이동합니다.
                .subscribe { launchMainActivity() }

        // 에러 메시지 이벤트를 구독합니다.
        viewDisposables += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message -> showError(message) }

        // 작업 진행 여부 이벤트를 구독합니다.
        viewDisposables += viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isLoading ->
                    // 작업 진행 여부 이벤트에 따라 프로그레스바의 표시 상태를 변경합니다.
                    if (isLoading) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                }

        // 기기에 저장되어 잇는 엑세스 토큰을 불러옵니다.
        disposables += viewModel.loadAccessToken()
        //] -- By viewmodel
    }

    override fun onDestroy() {
        LogMsg.w(TAG, "onDestroy()")
        super.onDestroy()
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

    /* //By lifecycle  onStop() 함수는 더 이상 오버라이드하지 않아도 됩니다.
    override fun onStop() {
        super.onStop()
        /*
        // 액티비티가 화면에서 사라지는 시점에 API 호출 객체가 생성되어 잇다면 API 요청을 취소합니다.
        accessTokenCall?.run { cancel() }
        // */
        //[ By RxJava
        // 관리하고 있던 디스포저블 객체를 모두 해제합니다.
        // accessTokenCall?.run { cancel() } 대신 사용합니다.
        disposables.clear()
        //]
    }
    // */

    private fun getAccessToken(code: String) {
        LogMsg.d(TAG, "getAccessToken()  code: $code")
        /*
        showProgress()

        // 이 줄이 실행될 때 accessTokenCall에 반환값이 저장됩니다.
        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성합니다.
        //
        // 앞에서 API 호출에 필요한 객체를 받았으므로, 이 시점에서 accessTokenCall 객체의 값은 널이 아닙니다.
        // 따라서 비 널 값 보증(!!)을 사용하여 이 객체를 사용합니다.
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

        /*
        //[ By RxJava
        // REST API를 통해 액세스 토큰을 요청합니다.
        // '+=' 연산자로 디스포저블을 CompositeDisposable에 추가합니다.
        //disposables.add(api.getAccessToken(
        disposables += api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

                // REST API를 통해 받은 응답에서 액세스 토큰만 추출합니다.
                .map { it.accessToken }

                // 이 이후에 수행되는 코드는 모두 메인 스레드에서 실행합니다.
                // RxAndroid에서 제공하는 스케줄러인 AndroidSchedulers.mainThread()를 사용합니다.
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
                }//)
        //]
        // */

        //[ By viewmodel
        // ViewModel에 정의된 함수를 사용하여 새로운 액세스 토큰을 요청합니다.
        disposables += viewModel.requestAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
        //]
    }

    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    /*
    private fun showError(throwable: Throwable) {
        //Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
        longToast(throwable.message ?: "No message available")
    }
    // */
    //[ By viewmodel
    private fun showError(message: String) {
        longToast(message)
    }
    //]

    private fun launchMainActivity() {
        //startActivity(Intent(
        //        this@SignInActivity, MainActivity::class.java)
        //        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        //        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }
}