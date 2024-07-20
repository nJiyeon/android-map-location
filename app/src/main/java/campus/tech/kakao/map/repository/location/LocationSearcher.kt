package campus.tech.kakao.map.repository.location

import android.content.Context
import campus.tech.kakao.map.model.Item

class LocationSearcher(private val context: Context) {
    fun search(keyword: String): List<Item> {
        val items = mutableListOf<Item>()
        val dbHelper = LocationDbHelper(context)
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            LocationContract.PLACE_NAME,
            LocationContract.ADDRESS_NAME,
            LocationContract.CATEGORY_GROUP_NAME,
            LocationContract.LATITUDE,
            LocationContract.LONGITUDE
        )

        val selection = "${LocationContract.CATEGORY_GROUP_NAME} = ?"
        val selectionArgs = arrayOf(keyword)
        val sortOrder = "${LocationContract.CATEGORY_GROUP_NAME} DESC"
        val cursor = db.query(
            LocationContract.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        with(cursor) {
            while (moveToNext()) {
                val placeName = getString(getColumnIndexOrThrow(LocationContract.PLACE_NAME))
                val addressName = getString(getColumnIndexOrThrow(LocationContract.ADDRESS_NAME))
                val categoryGroupName = getString(getColumnIndexOrThrow(LocationContract.CATEGORY_GROUP_NAME))
                val latitude = getDouble(getColumnIndexOrThrow(LocationContract.LATITUDE))
                val longitude = getDouble(getColumnIndexOrThrow(LocationContract.LONGITUDE))
                items.add(Item(placeName, addressName, categoryGroupName, latitude, longitude))
            }
            close()
        }

        return items
    }

    companion object {
        fun getInstance(context: Context): LocationSearcher {
            return LocationSearcher(context)
        }
    }
}