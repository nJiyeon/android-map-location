package campus.tech.kakao.map.viewmodel.keyword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import campus.tech.kakao.map.model.Item
import campus.tech.kakao.map.repository.keyword.KeywordRepository

class KeywordViewModel(private val keywordRepository: KeywordRepository) : ViewModel() {
    private val _keyword = MutableLiveData<List<String>>()
    val keyword: LiveData<List<String>>
        get() = _keyword

    private fun updateKeywordHistory(keyword: String) {
        keywordRepository.delete(keyword)
        keywordRepository.update(keyword)
        _keyword.value = keywordRepository.read()
    }

    private fun deleteKeywordHistory(keyword: String) {
        keywordRepository.delete(keyword)
        _keyword.value = keywordRepository.read()
    }

    fun readKeywordHistory() {
        _keyword.value = keywordRepository.read()
    }

    fun onSearchItemClick(item: Item) {
        updateKeywordHistory(item.place)
    }

    fun onKeywordItemDeleteClick(keyword: String) {
        deleteKeywordHistory(keyword)
    }
}
