package com.erank.yogappl.ui.fragments.events

import androidx.lifecycle.LiveData
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import javax.inject.Inject

class EventsViewModel @Inject constructor(val repository: Repository) {
    val user: User? = repository.currentUser

    fun getEvents(type: SourceType): LiveData<List<Event>> =
        repository.getEvents(type)

    suspend fun deleteEvent(event: Event) {
        repository.deleteEvent(event)
    }

    suspend fun toggleSignToEvent(event: Event) =
        repository.toggleSignToEvent(event)


    suspend fun getFilteredEvents(type: SourceType, query: String): List<Event> {
        return repository.getFilteredEvents(type, query)
    }


}