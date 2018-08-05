package denisa.com.smsapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import denisa.com.smsapplication.models.SMSObject


class Utils {

    companion object {
        fun getDialog(activity: Activity, sms: SMSObject) {
            activity?.let {
                val builder: AlertDialog.Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = AlertDialog.Builder(it, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    builder = AlertDialog.Builder(it)
                }
                builder.setTitle(activity.getString(R.string.new_message))
                        .setMessage(String.format(activity.getString(R.string.messages), sms.sender, sms.contents))
                        .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        .show()
            }
        }
    }

}
