package denisa.com.smsapplication.models

import android.os.Parcel
import android.os.Parcelable

class SMSObject(var sender: String?, var contents: String?, var timestamp: Long?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sender)
        parcel.writeString(contents)
        timestamp?.let { parcel.writeLong(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SMSObject> {
        override fun createFromParcel(parcel: Parcel): SMSObject {
            return SMSObject(parcel)
        }

        override fun newArray(size: Int): Array<SMSObject?> {
            return arrayOfNulls(size)
        }
    }


}
