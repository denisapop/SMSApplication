package denisa.com.smsapplication.SMS

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import android.telephony.SmsMessage
import android.util.Log

import denisa.com.smsapplication.MainActivity
import denisa.com.smsapplication.R
import denisa.com.smsapplication.models.SMSObject
import android.R.attr.mode
import android.content.ContentValues
import denisa.com.smsapplication.database.SMSContentProvider
import denisa.com.smsapplication.database.SMSDb
import android.app.NotificationChannel
import denisa.com.smsapplication.Constants


class SMService : JobIntentService() {

    companion object {

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, SMService::class.java, JOB_ID, work)
        }

        val JOB_ID = 1000
        val TAG = "SMSReciver"
    }

    override fun onHandleWork(intent: Intent) {

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var sms: SmsMessage? = null
            var smsObject: SMSObject? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsObject = SMSObject(smsMessage.displayOriginatingAddress, smsMessage.messageBody, smsMessage.timestampMillis)
                    sms = smsMessage
                }
            } else {
                val smsBundle = intent.extras
                if (smsBundle != null) {
                    val pdus = smsBundle.get("pdus") as Array<Any>
                    if (pdus == null) {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key")
                        return
                    }
                    val messages = arrayOfNulls<SmsMessage>(pdus.size)
                    var smsBody = ""
                    for (i in messages.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        smsBody += messages[i]?.messageBody
                    }
                    sms = messages[0]
                    smsObject = SMSObject(sms?.originatingAddress, smsBody, sms?.timestampMillis)

                }
            }
            smsObject?.let {
                sendNotification(it)
                insertInDatabase(it)
            }


        }
    }


    private fun sendNotification(msg: SMSObject) {
        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.SMS_OBJECT, msg)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
        val pendingIntent = PendingIntent.getActivity(this, uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_id_channel))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.new_message))
                .setContentText("$msg.contents   $msg.sender")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }


    private fun deleteSMS(message: SMSObject?) {
        val deleteUri = Uri.parse("content://sms")
        message?.let {
            contentResolver.delete(deleteUri, "address=? and date=?", arrayOf(message.sender, message.timestamp.toString()))
        }

    }

    private fun insertInDatabase(smsObject: SMSObject?) {
        smsObject?.let {
            val values = ContentValues()
            values.put(SMSDb.KEY_SENDER, smsObject.sender)
            values.put(SMSDb.KEY_CONTENT, smsObject.contents)
            values.put(SMSDb.KEY_TIMESTAMP, smsObject.timestamp)
            contentResolver.insert(SMSContentProvider.CONTENT_URI, values)
            deleteSMS(smsObject)
        }

    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_id_channel), name, importance)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}