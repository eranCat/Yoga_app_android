package com.erank.yogappl.data.injection

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataRepositoryModule::class])
@Singleton
interface AppComponent {

//    fun inject(activity: NewEditDataActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}