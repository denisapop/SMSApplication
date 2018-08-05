package denisa.com.smsapplication.SMS

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SMSReciver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        SMService.enqueueWork(context, intent)
    }


}