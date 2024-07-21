package campus.tech.kakao.map.viewmodel.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import campus.tech.kakao.map.model.Item

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _lastMarkerPosition = MutableLiveData<Item?>()
    val lastMarkerPosition: LiveData<Item?> get() = _lastMarkerPosition

    init {
        loadLastMarkerPosition()
    }

    fun saveLastMarkerPosition(item: Item) {
        with(sharedPreferences.edit()) {
            putFloat(PREF_LATITUDE, item.latitude.toFloat())
            putFloat(PREF_LONGITUDE, item.longitude.toFloat())
            putString(PREF_PLACE_NAME, item.place)
            putString(PREF_ROAD_ADDRESS_NAME, item.address)
            apply()
        }
        _lastMarkerPosition.value = item
    }

    private fun loadLastMarkerPosition() {
        if (sharedPreferences.contains(PREF_LATITUDE) && sharedPreferences.contains(PREF_LONGITUDE)) {
            val latitude = sharedPreferences.getFloat(PREF_LATITUDE, 0.0f).toDouble()
            val longitude = sharedPreferences.getFloat(PREF_LONGITUDE, 0.0f).toDouble()
            val placeName = sharedPreferences.getString(PREF_PLACE_NAME, "") ?: ""
            val roadAddressName = sharedPreferences.getString(PREF_ROAD_ADDRESS_NAME, "") ?: ""

            _lastMarkerPosition.value = if (placeName.isNotEmpty() && roadAddressName.isNotEmpty()) {
                Item(placeName, roadAddressName, "", latitude, longitude)
            } else {
                null
            }
        } else {
            _lastMarkerPosition.value = null
        }
    }

    companion object {
        private const val PREFS_NAME = "LastMarkerPrefs"
        private const val PREF_LATITUDE = "lastLatitude"
        private const val PREF_LONGITUDE = "lastLongitude"
        private const val PREF_PLACE_NAME = "lastPlaceName"
        private const val PREF_ROAD_ADDRESS_NAME = "lastRoadAddressName"
    }
}
