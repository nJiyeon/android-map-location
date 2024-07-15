package campus.tech.kakao.map.repository.keyword

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class KeywordDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.let { createDatabase(it) }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.let { upgradeDatabase(it, oldVersion, newVersion) }
    }

    private fun createDatabase(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    private fun upgradeDatabase(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        createDatabase(db)
    }

    companion object {
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${KeywordContract.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${KeywordContract.RECENT_KEYWORD} TEXT)"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${KeywordContract.TABLE_NAME}"

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "keyword.db"
    }
}
