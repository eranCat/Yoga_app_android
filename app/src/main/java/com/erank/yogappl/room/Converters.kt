package com.erank.yogappl.room

import androidx.room.TypeConverter
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.Money
import com.erank.yogappl.models.User
import com.erank.yogappl.utils.SMap
import com.erank.yogappl.utils.SSet
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.Status
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?) = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time

    @TypeConverter
    fun fromMoney(money: Money?) = money?.amount

    @TypeConverter
    fun toMoney(amount: Double?) = amount?.let { Money(it) }

    @TypeConverter
    fun fromUserLevel(level: User.Level?) = level?.name

    @TypeConverter
    fun toUserLevel(name: String?) = name?.let { User.Level.valueOf(it) }

    @TypeConverter
    fun fromDataLevel(level: BaseData.Level?) = level?.name

    @TypeConverter
    fun toDataLevel(name: String?) = name?.let { BaseData.Level.valueOf(it) }

    @TypeConverter
    fun fromStatus(status: Status?) = status?.name

    @TypeConverter
    fun toStatus(name: String?) = name?.let { Status.valueOf(it) }

    @TypeConverter
    fun fromUserType(type: User.Type?) = type?.name

    @TypeConverter
    fun toUserType(name: String?) = name?.let { User.Type.valueOf(it) }

    @TypeConverter
    fun fromDataType(type: DataType?) = type?.name

    @TypeConverter
    fun toDataType(name: String?): DataType? = name?.let { DataType.valueOf(it) }

    @TypeConverter
    fun fromLocation(latLng: LatLng?) = latLng?.let {
        Gson().toJson(it)
    }

    @TypeConverter
    fun toLocation(loc: String?) = loc?.let {
        Gson().fromJson(it, LatLng::class.java)
    }

    @TypeConverter
    fun toSet(json: String?): SSet? {
        val setType = object : TypeToken<SSet?>() {}.type
        return Gson().fromJson(json, setType)
    }

    @TypeConverter
    fun fromSet(set: SSet?): String? = Gson().toJson(set)

    @TypeConverter
    fun toSMap(json: String?): SMap<Any>? {
        val mapType = object : TypeToken<SMap<String>?>() {}.type
        return Gson().fromJson(json, mapType)
    }

    @TypeConverter
    fun fromSMap(map: SMap<String>?): String? = Gson().toJson(map)

    @TypeConverter
    fun toIMap(json: String?): SMap<Int>? {
        val mapType = object : TypeToken<SMap<Int>?>() {}.type
        return Gson().fromJson(json, mapType)
    }

    @TypeConverter
    fun fromIMap(map: SMap<Int>?): String? = Gson().toJson(map)
}