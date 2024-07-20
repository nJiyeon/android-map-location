package campus.tech.kakao.map.view


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import campus.tech.kakao.MyApplication
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
        val api = MyApplication.retrofit.create(KakaoLocalApi::class.java)

        // ViewModel 초기화
        searchViewModel = ViewModelProvider(this, SearchViewModelFactory(api))[SearchViewModel::class.java]
        keywordViewModel = ViewModelProvider(this, KeywordViewModelFactory(applicationContext))[KeywordViewModel::class.java]

        // 검색 결과 RecyclerView 설정
        searchAdapter = SearchAdapter(this)
        binding.searchResultView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        // 검색어 목록 RecyclerView 설정
        keywordAdapter = KeywordAdapter(this)
        binding.keywordHistoryView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = keywordAdapter
        }

        // 검색 입력 설정
        binding.searchTextInput.doAfterTextChanged {
            searchViewModel.searchLocationData(it.toString())
        }
        // 취소 버튼 클릭 이벤트 설정
        binding.deleteTextInput.setOnClickListener {
            binding.searchTextInput.text.clear()
        }

        // 검색어 목록 관찰
        keywordViewModel.keywords.observe(this) {
            keywordAdapter.submitList(it)
        }

        // 검색 결과 관찰하여 UI 업데이트
        searchViewModel.items.observe(this) {
            searchAdapter.submitList(it)
            binding.searchResultView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSearchItemClick(item: Item) {
        // 검색 항목 클릭 시 선택된 데이터를 반환하고 검색어 저장
        keywordViewModel.saveKeyword(item.place)
        val resultIntent = Intent().apply {
            putExtra("place_name", item.place)
            putExtra("road_address_name", item.address)
            putExtra("latitude", item.latitude)
            putExtra("longitude", item.longitude)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onKeywordItemClick(keyword: String) {
        // 저장된 검색어 클릭
        binding.searchTextInput.setText(keyword)
        searchViewModel.searchLocationData(keyword)
    }

    override fun onKeywordItemDeleteClick(keyword: String) {
        // 저장된 검색어 삭제
        keywordViewModel.deleteKeyword(keyword)
    }
}
