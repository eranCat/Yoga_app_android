package com.erank.yogappl.ui.fragments.lessons

import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.PreviewUser
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import javax.inject.Inject

class LessonsViewModel @Inject constructor(val repository: Repository) {
    val user: User?
        get() = repository.currentUser

    fun getLessons(type: SourceType) = repository.getLessons(type)

    suspend fun deleteLesson(lesson: Lesson) =
        repository.deleteLesson(lesson)

    suspend fun toggleSignToLesson(lesson: Lesson) =
        repository.toggleSignToLesson(lesson)

    suspend fun getUsersMap(list: List<Lesson>): Map<String, PreviewUser> {
        val ids = list.map { it.uid }.toSet()
        return repository.getUsers(ids)
    }

    suspend fun getFilteredLessons(type: SourceType, query: String) =
        repository.getFilteredLessons(type, query)

}