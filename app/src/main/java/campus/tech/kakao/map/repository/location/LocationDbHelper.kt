package campus.tech.kakao.map.repository.location

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class LocationDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${LocationContract.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${LocationContract.PLACE_NAME} TEXT," +
                    "${LocationContract.ADDRESS_NAME} TEXT," +
                    "${LocationContract.CATEGORY_GROUP_NAME} TEXT," +
                    "${LocationContract.LATITUDE} REAL," +
                    "${LocationContract.LONGITUDE} REAL)"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${LocationContract.TABLE_NAME}"

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "location.db"
    }
}
