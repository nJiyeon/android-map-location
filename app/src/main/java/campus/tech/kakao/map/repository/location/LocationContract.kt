package campus.tech.kakao.map.repository.location

import android.provider.BaseColumns

object LocationContract : BaseColumns {
    const val TABLE_NAME = "location"
    const val PLACE_NAME = "place_name"
    const val ADDRESS_NAME = "address_name"
    const val CATEGORY_GROUP_NAME = "category_group_name"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
}

