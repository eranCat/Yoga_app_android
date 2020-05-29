package com.erank.yogappl.data.injection

import android.content.Context
import com.erank.yogappl.ui.activities.location.LocationPickerActivity
import com.erank.yogappl.ui.activities.login.LoginActivity
import com.erank.yogappl.ui.activities.main.MainActivity
import com.erank.yogappl.ui.activities.newEditData.NewEditDataActivity
import com.erank.yogappl.ui.activities.register.RegisterActivity
import com.erank.yogappl.ui.activities.splash.SplashActivity
import com.erank.yogappl.ui.fragments.events.EventsListFragment
import com.erank.yogappl.ui.fragments.lessons.LessonsListFragment
import com.erank.yogappl.utils.App
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataRepositoryModule::class])
@Singleton
interface AppComponent {

    fun inject(activity: SplashActivity)
    fun inject(activity: NewEditDataActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: RegisterActivity)
    fun inject(fragment: EventsListFragment)
    fun inject(fragment: LessonsListFragment)
    fun inject(activity: LoginActivity)
    fun inject(activity: LocationPickerActivity)
    fun inject(app: App)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}