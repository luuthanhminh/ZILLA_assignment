package com.zilla.android.di.component

import com.zilla.android.di.module.ActivityModule
import com.zilla.android.ui.category.CategoryActivity
import com.zilla.android.ui.main.MainActivity
import com.zilla.android.ui.player.MusicPlayerActivity
import dagger.Component


@Component(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(activity: CategoryActivity)

    fun inject(activity: MainActivity)

    fun inject(activity: MusicPlayerActivity)

}