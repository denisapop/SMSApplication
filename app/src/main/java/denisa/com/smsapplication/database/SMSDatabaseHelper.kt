package denisa.com.smsapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SMSDatabaseHelper internal constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private val DATABASE_NAME = "SMS"
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        SMSDb.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        SMSDb.onUpgrade(db, oldVersion, newVersion)
    }


}

