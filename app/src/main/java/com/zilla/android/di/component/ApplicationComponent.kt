package com.zilla.android.di.component

import com.zilla.android.BaseApp
import com.zilla.android.di.module.ApplicationModule
import dagger.Component


@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(application: BaseApp)

}