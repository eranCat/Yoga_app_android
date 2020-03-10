package com.erank.yogappl.utils

import com.erank.yogappl.models.LocationResult
import com.erank.yogappl.utils.enums.TextFieldValidStates
import java.util.*

typealias SMap<V> = MutableMap<String, V>
typealias SSet = MutableSet<String>

typealias OnResponseRetrievedCallback<T> = (response: T) -> Unit
typealias OnLocationsFetchedCallback = OnResponseRetrievedCallback<List<LocationResult>>

typealias OnDateSet = (updatedDate: Date?) -> Unit
typealias DateValidationPredicate = (updated: Date?) -> TextFieldValidStates