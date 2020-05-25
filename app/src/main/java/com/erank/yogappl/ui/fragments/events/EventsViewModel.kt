package com.erank.yogappl.ui.fragments.events

import androidx.lifecycle.LiveData
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.interfaces.TaskCallback
import javax.inject.Inject

class EventsViewModel @Inject constructor(val repository: Repository) {
    val user: User? = repository.currentUser

    fun getEvents(type: SourceType): LiveData<List<Event>> =
        repository.getEvents(type)

    fun deleteEvent(event: Event, callback: TaskCallback<Int, Exception>) {
        repository.deleteEvent(event, callback)
    }

    fun toggleSignToEvent(event: Event, callback: TaskCallback<Boolean, Exception>) {
        repository.toggleSignToEvent(event, callback)
    }

    suspend fun getFilteredEvents(type: SourceType, query: String): List<Event> {
        return repository.getFilteredEvents(type, query)
    }


}