package com.erank.yogappl.utils.helpers

import android.location.Location
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.enums.SortType
import com.erank.yogappl.utils.extensions.minus

object SortingHelper {
    private val titleSorter =
        Comparator { o1: BaseData, o2: BaseData -> o1.title.compareTo(o2.title) }
    private val levelSorter =
        Comparator { o1: BaseData, o2: BaseData -> o1.level - o2.level }
    private val dateSorter =
        Comparator { o1: BaseData, o2: BaseData -> o1.startDate.compareTo(o2.startDate) }
    private val locationSorter =
        Comparator<BaseData> { o1: BaseData, o2: BaseData ->
            //                get current location
            val loc = LocationHelper.lastKnownLocation ?: return@Comparator 0

            val locationA = Location("point A").apply {
                val location = o1.location
                latitude = location.latitude
                longitude = location.longitude
            }

            val locationB = Location("point B").apply {
                val location = o2.location
                latitude = location.latitude
                longitude = location.longitude
            }

            val diff1 = locationA.distanceTo(loc)
            val diff2 = locationB.distanceTo(loc)

            diff1.compareTo(diff2)
        }

    fun getSorter(sortType: SortType?): java.util.Comparator<BaseData> {
        return when (sortType) {
            SortType.BEST -> locationSorter.thenComparing(dateSorter)
            SortType.LEVEL -> levelSorter
            SortType.DATE -> dateSorter
            SortType.NEAR -> locationSorter
            SortType.NAME -> titleSorter
            else -> titleSorter
        }
    }
}