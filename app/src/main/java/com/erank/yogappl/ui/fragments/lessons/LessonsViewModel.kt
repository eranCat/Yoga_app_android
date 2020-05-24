package com.erank.yogappl.ui.fragments.lessons

import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.PreviewUser
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.interfaces.TaskCallback
import javax.inject.Inject

class LessonsViewModel @Inject constructor(val repository: Repository) {
    val user: User? = repository.currentUser

    fun getLessons(type: SourceType) = repository.getLessons(type)

    fun deleteLesson(lesson: Lesson, callback: TaskCallback<Int, Exception>) {
        repository.deleteLesson(lesson, callback)
    }

    fun toggleSignToLesson(lesson: Lesson, callback: TaskCallback<Boolean, Exception>) {
        repository.toggleSignToLesson(lesson, callback)
    }

    suspend fun getUsersMap(list: List<Lesson>):Map<String,PreviewUser> {
        val ids = list.map { it.uid }.toSet()
        return repository.getUsers(ids)
    }

}