package com.androidhuman.example.simplegithub.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.ui.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*
// import 문에 startActivity 함수를 추가합니다.
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnActivityMainSearch.setOnClickListener {
            //startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            // 호출할 액티비티만 명시합니다.
            startActivity<SearchActivity>()
        }
    }
}
