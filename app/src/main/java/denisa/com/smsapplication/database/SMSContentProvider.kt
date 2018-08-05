package denisa.com.smsapplication.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils

class SMSContentProvider : ContentProvider() {

    private var dbHelper: SMSDatabaseHelper? = null

    // system calls onCreate() when it starts up the provider.
    override fun onCreate(): Boolean {
        // get access to the database helper
        dbHelper = SMSDatabaseHelper(context)
        return false
    }

    //Return the MIME type corresponding to a content URI
    override fun getType(uri: Uri): String? {

        return when (uriMatcher.match(uri)) {
            ALL_MESSAGES -> "vnd.android.cursor.dir/com.smsapplication.contentprovider.sms"
            SINGLE_MESSAGE -> "vnd.android.cursor.item/com.smsapplication.contentprovider.sms"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    //insert

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper?.writableDatabase
        when (uriMatcher.match(uri)) {
            ALL_MESSAGES -> {
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }//do nothing
        val id = db?.insert(SMSDb.SQLITE_TABLE, null, values)
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.parse(CONTENT_URI.toString() + "/" + id)
    }


    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {

        val db = dbHelper?.writableDatabase
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = SMSDb.SQLITE_TABLE

        when (uriMatcher.match(uri)) {
            ALL_MESSAGES -> {
            }
            SINGLE_MESSAGE -> {
                val id = uri.pathSegments[1]
                queryBuilder.appendWhere(SMSDb.KEY_ROWID + "=" + id)
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }//do nothing

        return queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder)

    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection

        val db = dbHelper!!.writableDatabase
        when (uriMatcher.match(uri)) {
            ALL_MESSAGES -> {
            }
            SINGLE_MESSAGE -> {
                val id = uri.pathSegments[1]
                selection = (SMSDb.KEY_ROWID + "=" + id
                        + if (!TextUtils.isEmpty(selection))
                    " AND (" + selection + ')'.toString()
                else
                    "")
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }//do nothing
        val deleteCount = db.delete(SMSDb.SQLITE_TABLE, selection, selectionArgs)
        context!!.contentResolver.notifyChange(uri, null)
        return deleteCount
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    companion object {

        private val ALL_MESSAGES = 1
        private val SINGLE_MESSAGE = 2

        private val AUTHORITY = "com.smsapplication.contentprovider"

        val CONTENT_URI = Uri.parse("content://$AUTHORITY/sms")

        private val uriMatcher: UriMatcher

        init {
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher.addURI(AUTHORITY, "sms", ALL_MESSAGES)
            uriMatcher.addURI(AUTHORITY, "sms/#", SINGLE_MESSAGE)
        }
    }

}

