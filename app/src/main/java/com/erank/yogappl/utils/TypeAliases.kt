package com.erank.yogappl.utils

import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.data.models.LocationResult
import java.util.*

typealias SMap<V> = MutableMap<String, V>
typealias SSet = MutableSet<String>

typealias OnDateSet = (updatedDate: Date?) -> Unit
typealias DateValidationPredicate = (updated: Date?) -> TextFieldValidStates