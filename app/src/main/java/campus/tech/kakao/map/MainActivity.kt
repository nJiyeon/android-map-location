package campus.tech.kakao.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextStyle
import campus.tech.kakao.map.model.Item
import campus.tech.kakao.map.repository.location.LocationSearcher
import campus.tech.kakao.map.view.SearchActivity
import campus.tech.kakao.map.viewmodel.keyword.KeywordViewModel
import campus.tech.kakao.map.viewmodel.keyword.KeywordViewModelFactory
import campus.tech.kakao.map.viewmodel.OnSearchItemClickListener
import campus.tech.kakao.map.viewmodel.OnKeywordItemClickListener

class MainActivity : AppCompatActivity(), OnSearchItemClickListener, OnKeywordItemClickListener {

    private lateinit var mapView: MapView
    private lateinit var errorLayout: RelativeLayout
    private lateinit var errorMessage: TextView
    private lateinit var errorDetails: TextView
    private lateinit var retryButton: ImageButton
    private lateinit var kakaoMap: KakaoMap
    private lateinit var labelLayer: LabelLayer
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var bottomSheetTitle: TextView
    private lateinit var bottomSheetAddress: TextView
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var searchResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var locationSearcher: LocationSearcher
    private lateinit var keywordViewModel: KeywordViewModel

    companion object {
        private const val PREFS_NAME = "LastMarkerPrefs"
        private const val PREF_LATITUDE = "lastLatitude"
        private const val PREF_LONGITUDE = "lastLongitude"
        private const val PREF_PLACE_NAME = "lastPlaceName"
        private const val PREF_ROAD_ADDRESS_NAME = "lastRoadAddressName"
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationSearcher = LocationSearcher(this)

        // ViewModel 초기화
        keywordViewModel = ViewModelProvider(this, KeywordViewModelFactory(applicationContext)).get(KeywordViewModel::class.java)

        // ActivityResultLauncher 초기화
        searchResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    val placeName = it.getStringExtra("place_name")
                    val roadAddressName = it.getStringExtra("road_address_name")
                    val latitude = it.getDoubleExtra("latitude", 0.0)
                    val longitude = it.getDoubleExtra("longitude", 0.0)

                    Log.d(TAG, "Search result: $placeName, $roadAddressName, $latitude, $longitude")

                    addLabel(placeName, roadAddressName, latitude, longitude)
                    if (placeName != null && roadAddressName != null) {
                        saveLastMarkerPosition(latitude, longitude, placeName, roadAddressName)
                    }
                }
            }
        }

        // MapView 초기화 및 맵 라이프사이클 콜백 설정
        mapView = findViewById(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d(TAG, "Map destroyed")
            }

            override fun onMapError(error: Exception) {
                Log.e(TAG, "Map error", error)
                showErrorScreen(error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                labelLayer = kakaoMap.labelManager?.layer!!
                Log.d(TAG, "Map is ready")
                loadLastMarkerPosition()
            }
        })

        // 검색창 클릭 시 검색 페이지로 이동
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchResultLauncher.launch(intent)
        }

        // 에러 화면 초기화
        errorLayout = findViewById(R.id.error_layout)
        errorMessage = findViewById(R.id.error_message)
        errorDetails = findViewById(R.id.error_details)
        retryButton = findViewById(R.id.retry_button)
        retryButton.setOnClickListener { onRetryButtonClick() }

        // BottomSheet 초기화
        bottomSheetLayout = findViewById(R.id.bottomSheetLayout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetTitle = findViewById(R.id.bottomSheetTitle)
        bottomSheetAddress = findViewById(R.id.bottomSheetAddress)

        // 처음에는 BottomSheet 숨기기
        bottomSheetLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()  // MapView의 resume 호출
        Log.d(TAG, "MapView resumed")
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()  // MapView의 pause 호출
        Log.d(TAG, "MapView paused")
    }

    private fun showErrorScreen(error: Exception) {
        errorLayout.visibility = View.VISIBLE
        errorMessage.text = getString(R.string.map_error_message)
        errorDetails.text = error.message
        mapView.visibility = View.GONE
    }

    private fun onRetryButtonClick() {
        errorLayout.visibility = View.GONE
        mapView.visibility = View.VISIBLE
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d(TAG, "Map destroyed on retry")
            }

            override fun onMapError(error: Exception) {
                Log.e(TAG, "Map error on retry", error)
                showErrorScreen(error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@MainActivity.kakaoMap = kakaoMap
                labelLayer = kakaoMap.labelManager?.layer!!
                Log.d(TAG, "Map is ready on retry")
                loadLastMarkerPosition()
            }
        })
    }

    private fun addLabel(placeName: String?, roadAddressName: String?, latitude: Double, longitude: Double) {
        if (placeName != null && roadAddressName != null) {
            val position = LatLng.from(latitude, longitude)
            val styles = kakaoMap.labelManager?.addLabelStyles(
                LabelStyles.from(
                    LabelStyle.from(R.drawable.pin).setZoomLevel(1),
                    LabelStyle.from(R.drawable.pin)
                        .setTextStyles(
                            LabelTextStyle.from(this, R.style.labelTextStyle)
                        )
                        .setZoomLevel(1)
                )
            )

            labelLayer.addLabel(
                LabelOptions.from(placeName, position).setStyles(styles).setTexts(placeName)
            )

            moveCamera(position)
            saveLastMarkerPosition(latitude, longitude, placeName, roadAddressName)
            updateBottomSheet(placeName, roadAddressName)
        }
    }

    private fun moveCamera(position: LatLng) {
        kakaoMap.moveCamera(
            CameraUpdateFactory.newCenterPosition(position),
            CameraAnimation.from(10, false, false)
        )
    }

    private fun saveLastMarkerPosition(latitude: Double, longitude: Double, placeName: String, roadAddressName: String) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putFloat(PREF_LATITUDE, latitude.toFloat())
            putFloat(PREF_LONGITUDE, longitude.toFloat())
            putString(PREF_PLACE_NAME, placeName)
            putString(PREF_ROAD_ADDRESS_NAME, roadAddressName)
            apply()
        }
    }

    private fun loadLastMarkerPosition() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(PREF_LATITUDE) && sharedPreferences.contains(PREF_LONGITUDE)) {
            val latitude = sharedPreferences.getFloat(PREF_LATITUDE, 0.0f).toDouble()
            val longitude = sharedPreferences.getFloat(PREF_LONGITUDE, 0.0f).toDouble()
            val placeName = sharedPreferences.getString(PREF_PLACE_NAME, "") ?: ""
            val roadAddressName = sharedPreferences.getString(PREF_ROAD_ADDRESS_NAME, "") ?: ""

            if (placeName.isNotEmpty() && roadAddressName.isNotEmpty()) {
                Log.d(TAG, "Loaded last marker position: lat=$latitude, lon=$longitude, placeName=$placeName, roadAddressName=$roadAddressName")
                addLabel(placeName, roadAddressName, latitude, longitude)
                val position = LatLng.from(latitude, longitude)
                moveCamera(position)
                updateBottomSheet(placeName, roadAddressName)
            } else {
                Log.d(TAG, "No place name or road address name found")
            }
        } else {
            Log.d(TAG, "No last marker position found in SharedPreferences")
        }
    }

    private fun updateBottomSheet(placeName: String, roadAddressName: String) {
        bottomSheetTitle.text = placeName
        bottomSheetAddress.text = roadAddressName
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetLayout.visibility = View.VISIBLE
    }

    override fun onKeywordItemClick(keyword: String) {
        // 아무 작업도 수행하지 않음
    }

    override fun onKeywordItemDeleteClick(keyword: String) {
        // 아무 작업도 수행하지 않음
    }

    override fun onSearchItemClick(item: Item) {
        // 검색 결과 목록에서 항목 선택 시 이벤트처리
        addLabel(item.place, item.address, item.latitude, item.longitude)
        saveLastMarkerPosition(item.latitude, item.longitude, item.place, item.address)
    }
}
