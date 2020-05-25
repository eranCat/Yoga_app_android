package com.erank.yogappl.data.models

import android.os.Parcel
import android.os.Parcelable
import com.erank.yogappl.utils.SMap
import com.erank.yogappl.utils.helpers.MoneyConverter
import com.google.firebase.firestore.Exclude
import java.text.NumberFormat
import javax.inject.Inject

class Money(var amount: Double) : Parcelable {
    constructor() : this(0.0)

    constructor(map: SMap<Double>) : this() {
        encoded = map
    }

    @Inject
    lateinit var moneyConverter:MoneyConverter

    var encoded: SMap<Double>
        get() = mutableMapOf("amount" to moneyConverter.convertFromLocaleToDefault(amount))
        set(value) {
            amount = moneyConverter.convertFromDefaultToLocale(value["amount"]!!)
        }

    @Exclude
    operator fun minus(other: Money) = amount - other.amount

    @Exclude
    override fun toString(): String = if (amount <= 0.0) "Free"
    else NumberFormat.getCurrencyInstance().format(amount)


    constructor(source: Parcel) : this(source.readDouble())

    @Exclude
    override fun describeContents() = 0

    @Exclude
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeDouble(amount)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Money> = object : Parcelable.Creator<Money> {
            override fun createFromParcel(source: Parcel): Money = Money(source)
            override fun newArray(size: Int): Array<Money?> = arrayOfNulls(size)
        }
    }
}
