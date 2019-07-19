package com.androidhuman.example.simplegithub.ui.search

//[ By viewmodel
import android.arch.lifecycle.ViewModelProviders
//]
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.GithubApi
import com.androidhuman.example.simplegithub.api.model.GithubRepo
//import com.androidhuman.example.simplegithub.api.provideGithubApi
import com.androidhuman.example.simplegithub.data.SearchHistoryDao
//[ By room
//import com.androidhuman.example.simplegithub.data.provideSearchHistoryDao
//]
// 연산자 오버로딩 함수를 import 문에 추가합니다.
import com.androidhuman.example.simplegithub.extensions.plusAssign
//[ By room
import com.androidhuman.example.simplegithub.extensions.runOnIoScheduler
//]
//[ By lifecycle
import com.androidhuman.example.simplegithub.rx.AutoClearedDisposable
//]
import com.androidhuman.example.simplegithub.ui.repo.RepositoryActivity
import com.androidhuman.example.simplegithub.util.LogMsg
//[ By RxJava-rxBinding
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Completable
//]
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
// import 문에 startActivity 함수를 추가합니다.
import org.jetbrains.anko.startActivity
import javax.inject.Inject

// AppCompatActivity 대신 DaggerAppCompatActivity를 상속합니다.
class SearchActivity : DaggerAppCompatActivity(), SearchAdapter.ItemClickListener {

    companion object {
        const val TAG = "SearchActivity"
    }

    // 프로퍼티에 lateinit을 추가합니다.
    internal lateinit var menuSearch: MenuItem

    internal lateinit var searchView: SearchView

    /*
    // lazy 프로퍼티로 전환합니다.
    internal val adapter by lazy {
        // apply() 함수를 사용하여 객체 생성과 함수 호출을 한번에 수행합니다.
        SearchAdapter().apply { setItemClickListener(this@SearchActivity) }
    }
    // */

    /*
    internal val api by lazy { provideGithubApi(this) }

    //[ By room
    // SearchHistoryDao의 인스턴스를 받아옵니다.
    internal val searchHistoryDao by lazy { provideSearchHistoryDao(this) }
    //]
    // */

    /*
    // 널 값을 허용하도록 한 후, 초기값을 명시적으로 null로 지정합니다.
    internal var searchCall: Call<RepoSearchResponse>? = null
    // */
    /*
    //[ By RxJava
    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화합니다.
    // internal var searchCall: Call<RepoSearchResponse>? = null 대신 사용합니다.
    internal val disposables = CompositeDisposable()
    //]
    // */
    //[ By lifecycle  CompositeDisposable에서 AutoClearedDisposable로 변경합니다.
    internal val disposables = AutoClearedDisposable(this)
    //]

    /*
    //[ By RxJava-rxBinding
    // viewDisposables 프로퍼티를 추가합니다.
    internal val viewDisposables = CompositeDisposable()
    //]
    // */
    //[ By lifecycle
    // CompositeDisposable에서 AutoClearedDisposable로 변경합니다.
    // 액티비티가 완전히 종료되기 전까지 이벤트를 계속 받기 위해 추가합니다.
    internal val viewDisposables
            = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)
    // */

    /*
    //[ By viewmodel
    // SearchViewModel를 생성할 때 필요한 뷰모델 팩토리 클래스의 인스턴스를 생성합니다.
    internal val viewModelFactory by lazy {
        //SearchViewModelFactory(provideGithubApi(this), provideSearchHistoryDao(this))
        //[ By dagger_1
        // 대거를 통해 주입받은 객체를 생성자의 인자로 전달합니다.
        SearchViewModelFactory(githubApi, searchHistoryDao)
        //]
    }
    // */
    //[ ++ By dagger_2
    // 대거로부터 SearchAdapter 객체를 주입받습니다.
    @Inject lateinit var adapter: SearchAdapter

    // 대거로부터 SearchViewModelFactory 객체를 주입받습니다.
    @Inject lateinit var viewModelFactory: SearchViewModelFactory
    //] -- By dagger_2

    // 뷰모델의 인스턴스는 onCreate()에서 받으므로, lateinit으로 선언합니다.
    lateinit var viewModel: SearchViewModel
    //]

    /*
    //[ ++ By dagger_1
    // 대거를 통해 GithubApi 객체를 주입받는 프로퍼티를 선언합니다.
    @Inject
    lateinit var githubApi: GithubApi

    // 대거를 통해 SearchHistoryDao 객체를 주입받는 프로퍼티를 선언합니다.
    @Inject
    lateinit var searchHistoryDao: SearchHistoryDao
    //] -- By dagger_1
    // */

    override fun onCreate(savedInstanceState: Bundle?) {
        LogMsg.w(TAG, "onCreate()  savedInstanceState: $savedInstanceState");
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //[ By viewmodel
        // SearchViewModel의 인스턴스를 받습니다.
        viewModel = ViewModelProviders.of(
                this, viewModelFactory)[SearchViewModel::class.java]
        //]

        //[ By lifecycle  Lifecycle.addObserver() 함수를 사용하여 각 AutoClearedDisposable 객체를 옵서버로 등록합니다.
        lifecycle += disposables

        // viewDisposables에서 이 액티비티의 생명주기 이벤트를 받도록 합니다.
        lifecycle += viewDisposables
        //]

        // with() 함수를 사용하여 rvActivitySearchList 범위 내에서 작업을 수행합니다.
        with(rvActivitySearchList) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }

        //[ ++ By viewmodel
        // 검색 결과 이벤트를 구독합니다.
        viewDisposables += viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items ->
                    with(adapter) {
                        LogMsg.d(TAG, "subscribe()  items.isEmpty: ${items.isEmpty}")
                        if (items.isEmpty) {
                            LogMsg.v(TAG, "clearItems()")
                            clearItems()
                        } else {
                            LogMsg.v(TAG, "setItems()")
                            setItems(items.value)
                        }
                        notifyDataSetChanged()
                    }
                }

        // 메시지 이벤트를 구독합니다.
        viewDisposables += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message ->
                    if (message.isEmpty) {
                        hideError()
                    } else {
                        showError(message.value)
                    }
                }


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
        //] -- By viewmodel
    }

    override fun onDestroy() {
        LogMsg.w(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun onStart() {
        LogMsg.w(TAG, "onStart()")
        super.onStart()
    }

    override fun onStop() {
        LogMsg.w(TAG, "onStop()")
        super.onStop()
    }

    override fun onResume() {
        LogMsg.w(TAG, "onResume()")
        super.onResume()
    }

    override fun onPause() {
        LogMsg.w(TAG, "onPause()")
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        LogMsg.w(TAG, "onCreateOptionsMenu()")
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)

        /*
        // menuSearch.actionView를 SearchView로 캐스팅합니다.
        // apply() 함수를 사용하여 객체 생성과 리스너 지정을 동시에 수행합니다.
        searchView = (menuSearch.actionView as SearchView).apply {
            // SearchView.OnQueryTextListener 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성합니다.
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    updateTitle(query)
                    hideSoftKeyboard()
                    collapseSearchView()
                    searchRepository(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }
        // */

        //[ ++ By RxJava-rxBinding
        searchView = (menuSearch.actionView as SearchView)

        // SearchView에서 발생하는 이벤트를 옵서버블 형태로 받습니다.
        //viewDisposables += RxSearchView.queryTextChangeEvents(searchView)
        // SearchView 인스턴스에서 RxBinding에서 제공하는 함수를 직접 호출합니다.
        viewDisposables += searchView.queryTextChangeEvents()

                // 검색을 수행했을 때 발생한 이벤트만 받습니다.
                .filter { it.isSubmitted }

                // 이벤트에서 검색어 텍스트(CharSequence)를 추출합니다.
                .map { it.queryText() }

                // 빈 문자열이 아닌 검색어만 받습니다.
                .filter { it.isNotEmpty() }

                // 검색어를 String 형태로 변환합니다.
                .map { it.toString() }

                // 이 이후에 수행되는 코드는 모두 메인 스레드에서 실행합니다.
                // RxAndroid에서 제공하는 스케줄러인 AndroidSchedulers.mainThread()를 사용합니다.
                .observeOn(AndroidSchedulers.mainThread())

                // 옵서버블을 구독합니다.
                .subscribe { query ->

                    // 검색 절차를 수행합니다.
                    updateTitle(query)
                    hideSoftKeyboard()
                    collapseSearchView()
                    searchRepository(query)
                }
        //] -- By RxJava-rxBinding

        /*
        // with() 함수를 사용하여 menuSearch 범위 내에서 작업을 수행합니다.
        with(menuSearch) {
            setOnActionExpandListener(object: MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true;
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    if ("" == searchView.query) {
                        finish()
                    }
                    return true
                }

            })

            expandActionView()
        }
        // */
        //[ ++ By viewmodel
        // 마지막으로 검색한 검색어 이벤트를 구독합니다.
        viewDisposables += viewModel.lastSearchKeyword
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { keyword ->
                    if (keyword.isEmpty) {
                        // 아직 검색을 수행하지 않은 경우 SearchView를 펼친 상태로 유지합니다.
                        menuSearch.expandActionView()
                    } else {
                        // 검색어가 있는 경우 해당 검색어를 액티비티의 제목으로 표시합니다.
                        updateTitle(keyword.value)
                    }
                }
        //] -- By viewmodel


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_search_query == item.itemId) {
            item.expandActionView()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /* //By lifecycle  onStop() 함수는 더 이상 오버라이드하지 않아도 됩니다.
    override fun onStop() {
        super.onStop()
        /*
        // 액티비티가 화면에서 사라지는 시점에 API 호출 객체가 생성되어 잇다면 API 요청을 취소합니다.
        searchCall?.run { cancel() }
        // */
        //[ By RxJava
        // 관리하고 있던 디스포저블 객체를 모두 해제합니다.
        // searchCall?.run { cancel() } 대신 사용합니다.
        disposables.clear()
        //]

        //[ By RxJava-rxBinding
        // 액티비티가 완전히 종료되고 있는 경우에만 관리하고 있는 디스포저블을 해제합니다.
        // 화면이 꺼지거나 다른 액티비티를 호출하여 액티비티가 화면에서 사라지는 경우에는 해제하지 않습니다.
        if (isFinishing) {
            viewDisposables.clear()
        }
        //]
    }
    // */

    override fun onItemClick(repository: GithubRepo) {
        /*
        //[ ++ By room
        /*
        // 데이터베이스에 저장소를 추가합니다.
        // 데이터 조작 코드를 메인 스레드에서 호출하면 에러가 발생하므로,
        // RxJava의 Completable을 사용하여
        // IO 스레드에서 데이터 추가 작업을 수행하도록 합니다.
        disposables += Completable
                .fromCallable{ searchHistoryDao.add(repository) }
                .subscribeOn(Schedulers.io())
                .subscribe()
        // */
        // runOnIoScheduler 함수로 IO 스케줄러에서 실행할 작업을 간단히 표현합니다.
        disposables += runOnIoScheduler { searchHistoryDao.add(repository) }
        //] -- By room
        // */
        //[ By viewmodel
        // 선택한 저장소 정보를 데이터베이스에 추가합니다.
        disposables += viewModel.addToSearchHistory(repository)
        //]

        /*
        // apply() 함수를 사용하여 객체 생성과 extra를 추가하는 작업을 동시에 수행합니다.
        val intent = (Intent(this, RepositoryActivity::class.java)).apply {
            putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
            putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        }
        startActivity(intent)
        // */
        // 부가정보로 전달할 항목을 함수의 인자로 바로 넣어줍니다.
        startActivity<RepositoryActivity>(
                RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
                RepositoryActivity.KEY_REPO_NAME to repository.name)
    }

    private fun searchRepository(query: String) {
        /*
        clearResults()
        hideError()
        showProgress()

        // 이 줄이 실행될 때 searchCall에 반환값이 저장됩니다.
        searchCall = api.searchRepository(query)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성합니다.
        //
        // 앞에서 API 호출에 필요한 객체를 받았으므로, 이 시점에서 searchCall 객체의 값은 널이 아닙니다.
        // 따라서 비 널 값 보증(!!)을 사용하여 이 객체를 사용합니다.
        searchCall!!.enqueue(object : Callback<RepoSearchResponse> {
            override fun onResponse(call: Call<RepoSearchResponse>,
                                    response: Response<RepoSearchResponse>) {
                hideProgress()

                val searchResult = response.body()
                if (response.isSuccessful && null != searchResult) {
                    // with() 함수를 사용하여 adapter 범위 내에서 작업을 수행합니다.
                    with(adapter) {
                        setItems(searchResult.items)
                        notifyDataSetChanged()
                    }

                    if (0 == searchResult.totalCount) {
                        showError(getString(R.string.no_search_result))
                    }
                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<RepoSearchResponse>, t: Throwable) {
                hideProgress()
                // showError() 함수는 널 값을 허용하지 않으나 t.message는 널 값을 반환할 수 있습니다.
                showError(t.message)
            }
        })
        // */

        /*
        //[ ++ By RxJava
        // REST API를 통해 검색 결과를 요청합니다.
        // '+=' 연산자로 디스포저블을 CompositeDisposable에 추가합니다.
        //disposables.add(api.searchRepository(query)
        disposables += api.searchRepository(query)
                // Observable 형태로 결과를 바꿔주기 위해 flatMap을 사용합니다.
                .flatMap {
                    if (0 == it.totalCount) {
                        // 검색 결과가 없을 경우
                        // 에러를 발생시켜 에러 메시지를 표시하도록 합니다.
                        // (곧바로 에러 블록이 실행됩니다.)
                        Observable.error(IllegalStateException("No search result"))
                    } else {
                        // 검색 결과 리스트를 다음 스트림으로 전달합니다.
                        Observable.just(it.items)
                    }
                }

                // 이 이후에 수행되는 코드는 모두 메인 스레드에서 실행합니다.
                // RxAndroid에서 제공하는 스케줄러인
                // AndroidSchedulers.mainThread()를 사용합니다.
                .observeOn(AndroidSchedulers.mainThread())

                // 구독할 때 수행할 작업을 구현합니다.
                .doOnSubscribe {
                    clearResults()
                    hideError()
                    showProgress()
                }

                // 스트림이 종료될 때 수행할 작업을 구현합니다.
                .doOnTerminate { hideProgress() }

                // 옵서버블을 구독합니다.
                .subscribe({ items ->
                    // API를 통해 액세스 토큰을 정상적으로 받았을 때 처리할 작업을 구현합니다.
                    // 작업 중 오류가 발생하면 이 블록은 호출되지 않습니다.
                    with(adapter) {
                        setItems(items)
                        notifyDataSetChanged()
                    }
                }) {
                    // 에러블록
                    // 네트워크 오류나 데이터 처리 오류 등
                    // 작업이 정상적으로 완료되지 않았을 때 호출됩니다.
                    showError(it.message)
                }//)
        //] -- By RxJava
        // */
        //[ By viewmodel
        // 전달받은 검색어로 검색 결과를 요청합니다.
        disposables += viewModel.searchRepository(query)
        //]
    }

    private fun updateTitle(query: String) {
        // 별도의 변수 선언 없이, getSupportActionBar()의 반환값이 널이 아닌 경우에만 작업을 수행합니다.
        supportActionBar?.run { subtitle = query }
    }

    private fun hideSoftKeyboard() {
        // 별도의 변수 선언 없이, 획득한 인스턴스의 범위 내에서 작업을 수행합니다.
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    /*
    private fun clearResults() {
        // with() 함수를 사용하여 adapter 범위 내에서 작업을 수행합니다.
        with(adapter) {
            clearItems()
            notifyDataSetChanged()
        }
    }
    // */

    private fun showProgress() {
        pbActivitySearch.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pbActivitySearch.visibility = View.GONE
    }

    private fun showError(message: String?) {
        // with() 함수를 사용하여 tvActivitySearchMessage 범위 내에서 작업을 수행합니다.
        with(tvActivitySearchMessage) {
            // message가 널 값인 경우 "Unexpected error." 메시지를 표시합니다.
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        // with() 함수를 사용하여 tvActivitySearchMessage 범위 내에서 작업을 수행합니다.
        with(tvActivitySearchMessage) {
            text = ""
            visibility = View.GONE
        }
    }
}
