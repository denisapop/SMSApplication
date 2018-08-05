package denisa.com.smsapplication

import android.Manifest
import android.content.CursorLoader
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.database.Cursor
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.SimpleCursorAdapter
import denisa.com.smsapplication.database.SMSContentProvider
import denisa.com.smsapplication.database.SMSDb
import denisa.com.smsapplication.models.SMSObject
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
        android.app.LoaderManager.LoaderCallbacks<Cursor> {

    var simpleCursorAdapter: SimpleCursorAdapter? = null
    val SMS_PERMISSION_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        intent?.let { onNewIntent(intent) }
        if (!isSmsPermissionGranted())
            showRequestPermissionsInfoAlertDialog(true)

        displayListView()
    }

    fun displayListView() {


        val columns = arrayOf<String>(SMSDb.KEY_SENDER, SMSDb.KEY_CONTENT)
        val texts: IntArray = intArrayOf(R.id.sender, R.id.content)

        simpleCursorAdapter = SimpleCursorAdapter(this, R.layout.sms_info, null, columns, texts, 0);


        simpleCursorAdapter?.let {
            smsList?.adapter = it
        }
        loaderManager.initLoader(0, null, this);
    }


    override fun onCreateLoader(id: Int, args: Bundle?): android.content.Loader<Cursor>? {
        val projection = arrayOf<String>(SMSDb.KEY_ROWID, SMSDb.KEY_SENDER, SMSDb.KEY_CONTENT, SMSDb.KEY_TIMESTAMP)
        return CursorLoader(this,
                SMSContentProvider.CONTENT_URI, projection, null, null, null)
    }

    override fun onLoadFinished(loader: android.content.Loader<Cursor>?, data: Cursor?) {
        simpleCursorAdapter?.swapCursor(data)
    }

    override fun onLoaderReset(loader: android.content.Loader<Cursor>?) {
        simpleCursorAdapter?.swapCursor(null)
    }


    fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.READ_SMS)) {

            return
        }
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),
                SMS_PERMISSION_CODE)
    }

    fun showRequestPermissionsInfoAlertDialog(makeSystemRequest: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.important))
        builder.setMessage(getString(R.string.dialog_text))

        builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
            dialog.dismiss()
            if (makeSystemRequest) {
                requestReadAndSendSmsPermission()
            }
        }

        builder.setCancelable(false)
        builder.show()

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            SMS_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //todo

                } else {
                    //todo
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Starts a new or restarts an existing Loader in this manager
        loaderManager.restartLoader(0, null, this)
    }


    override fun onNewIntent(intent: Intent) {

        intent?.extras?.let {
            var smsObject: SMSObject? = it.getParcelable(Constants.SMS_OBJECT)
            smsObject?.let {
                Utils.getDialog(this, smsObject)
            }
        }
    }
}