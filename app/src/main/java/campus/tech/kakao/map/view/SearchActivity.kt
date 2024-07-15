package campus.tech.kakao.map.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import campus.tech.kakao.map.BuildConfig
import campus.tech.kakao.map.adapter.keyword.KeywordAdapter
import campus.tech.kakao.map.adapter.search.SearchAdapter
import campus.tech.kakao.map.api.KakaoLocalApi
import campus.tech.kakao.map.databinding.ActivitySearchBinding
import campus.tech.kakao.map.model.Item
import campus.tech.kakao.map.viewmodel.OnKeywordItemClickListener
import campus.tech.kakao.map.viewmodel.OnSearchItemClickListener
import campus.tech.kakao.map.viewmodel.keyword.KeywordViewModel
import campus.tech.kakao.map.viewmodel.keyword.KeywordViewModelFactory
import campus.tech.kakao.map.viewmodel.search.SearchViewModel
import campus.tech.kakao.map.viewmodel.search.SearchViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), OnSearchItemClickListener, OnKeywordItemClickListener {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var keywordViewModel: KeywordViewModel
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var keywordAdapter: KeywordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoLocalApi::class.java)

        // ViewModel 초기화
        searchViewModel = ViewModelProvider(this, SearchViewModelFactory(api))[SearchViewModel::class.java]
        keywordViewModel = ViewModelProvider(this, KeywordViewModelFactory(applicationContext))[KeywordViewModel::class.java]

        // RecyclerView 설정 (검색 결과)
        searchAdapter = SearchAdapter(this)
        binding.searchResultView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        // RecyclerView 설정 (최근 검색어)
        keywordAdapter = KeywordAdapter(this)
        binding.keywordHistoryView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = keywordAdapter
        }

        // 검색 입력 설정
        binding.searchTextInput.doAfterTextChanged {
            searchViewModel.searchLocationData(it.toString())
        }

        // 텍스트 입력 삭제 버튼 설정
        binding.deleteTextInput.setOnClickListener {  // 삭제 버튼 클릭 이벤트 설정
            binding.searchTextInput.text.clear()  // 검색어 입력란 텍스트 삭제
        }

        // 데이터 관찰하여 UI 업데이트 (검색 결과)
        searchViewModel.items.observe(this) {
            searchAdapter.submitList(it)
            binding.searchResultView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        // 데이터 관찰하여 UI 업데이트 (최근 검색어)
        keywordViewModel.keyword.observe(this) {
            keywordAdapter.submitList(it)
        }

        // 최근 검색어 읽기
        keywordViewModel.readKeywordHistory()
    }

    override fun onSearchItemClick(item: Item) {
        keywordViewModel.onSearchItemClick(item)
    }

    override fun onKeywordItemDeleteClick(keyword: String) {
        keywordViewModel.onKeywordItemDeleteClick(keyword)
    }
}
