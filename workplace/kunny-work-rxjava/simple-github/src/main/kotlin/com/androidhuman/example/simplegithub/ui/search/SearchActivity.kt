package com.androidhuman.example.simplegithub.ui.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.model.GithubRepo
import com.androidhuman.example.simplegithub.api.model.RepoSearchResponse
import com.androidhuman.example.simplegithub.api.provideGithubApi
import com.androidhuman.example.simplegithub.ui.repo.RepositoryActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {
    // 프로퍼티에 lateinit을 추가합니다.
    internal lateinit var menuSearch: MenuItem

    internal lateinit var searchView: SearchView

    internal val adapter by lazy {
        SearchAdapter().apply { setItemClickListener(this@SearchActivity) }
    }

    internal val api by lazy { provideGithubApi(this) }

    /*
    internal var searchCall: Call<RepoSearchResponse>? = null
    */
    // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화합니다.
    // internal var searchCall: Call<RepoSearchResponse>? = null 대신 사용합니다.
    internal val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        with(rvActivitySearchList) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)

        // menuSearch.actionView를 SearchView로 캐스팅합니다.
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

        menuSearch.expandActionView()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_search_query == item.itemId) {
            item.expandActionView()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        /*
        searchCall?.run { cancel() }
        */
        // 관리하고 있던 디스포저블 객체를 모두 해제합니다.
        // searchCall?.run { cancel() } 대신 사용합니다.
        disposables.clear()
    }

    override fun onItemClick(repository: GithubRepo) {
        //val intent = (Intent(this, RepositoryActivity::class.java)).apply {
        //    putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
        //    putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        //}
        //startActivity(intent)

        startActivity<RepositoryActivity>(
                RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
                RepositoryActivity.KEY_REPO_NAME to repository.name)
    }

    private fun searchRepository(query: String) {
        /*
        clearResults()
        hideError()
        showProgress()

        searchCall = api.searchRepository(query)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성합니다.
        searchCall!!.enqueue(object : Callback<RepoSearchResponse> {
            override fun onResponse(call: Call<RepoSearchResponse>,
                                    response: Response<RepoSearchResponse>) {
                hideProgress()

                val searchResult = response.body()
                if (response.isSuccessful && null != searchResult) {
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

        // REST API를 통해 검색 결과를 요청합니다.
        disposables.add(api.searchRepository(query)
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
                }
        )
    }

    private fun updateTitle(query: String) {
        supportActionBar?.run { subtitle = query }
    }

    private fun hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    private fun clearResults() {
        with(adapter) {
            clearItems()
            notifyDataSetChanged()
        }
    }

    private fun showProgress() {
        pbActivitySearch.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pbActivitySearch.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(tvActivitySearchMessage) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        with(tvActivitySearchMessage) {
            text = ""
            visibility = View.GONE
        }
    }
}
