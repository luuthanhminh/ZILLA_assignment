package com.zilla.android.di.module

import android.app.Activity
import com.zilla.android.api.ApiServiceInterface
import com.zilla.android.ui.category.CategoryContract
import com.zilla.android.ui.category.CategoryPresenter
import com.zilla.android.ui.main.MainContract
import com.zilla.android.ui.main.MainPresenter
import com.zilla.android.ui.player.MusicPlayerContract
import com.zilla.android.ui.player.MusicPlayerPresenter
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private var activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    fun provideApiService(): ApiServiceInterface {
        return ApiServiceInterface.create()
    }

    @Provides
    fun providePresenter(): MainContract.Presenter {
        return MainPresenter()
    }

    @Provides
    fun provideCategoryPresenter(): CategoryContract.Presenter {
        return CategoryPresenter()
    }

    @Provides
    fun provideMusicPlayerPresenter(activity: Activity): MusicPlayerContract.Presenter {
        return MusicPlayerPresenter(activity)
    }

}