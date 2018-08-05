package denisa.com.smsapplication.database

import android.database.sqlite.SQLiteDatabase
import android.util.Log

object SMSDb {

    val KEY_ROWID = "_id"
    val KEY_SENDER = "sender"
    val KEY_CONTENT = "content"
    val KEY_TIMESTAMP = "stamp"

    private val LOG_TAG = "SMSDb"
    val SQLITE_TABLE = "SMS"

    private val DATABASE_CREATE = "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
            KEY_ROWID + " integer PRIMARY KEY autoincrement," +
            KEY_SENDER + "," +
            KEY_CONTENT + "," +
            KEY_TIMESTAMP + ");"

    fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DATABASE_CREATE)
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $SQLITE_TABLE")
        onCreate(db)
    }

}