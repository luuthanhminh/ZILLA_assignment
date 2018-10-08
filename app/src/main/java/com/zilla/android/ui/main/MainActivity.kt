package com.zilla.android.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.zilla.android.R
import com.zilla.android.di.component.DaggerActivityComponent
import com.zilla.android.di.module.ActivityModule
import javax.inject.Inject

class MainActivity: AppCompatActivity(), MainContract.View {

    @Inject lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        injectDependency()

        presenter.attach(this)
    }


    override fun showAboutFragment() {

    }

    override fun showListFragment() {
//        supportFragmentManager.beginTransaction()
//                .disallowAddToBackStack()
//                .setCustomAnimations(AnimType.SLIDE.getAnimPair().first, AnimType.SLIDE.getAnimPair().second)
//                .replace(R.id.frame, ListFragment().newInstance(), ListFragment.TAG)
//                .commit()
    }


    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .build()
        activityComponent.inject(this)
    }


    enum class AnimType() {
        SLIDE,
        FADE;

        fun getAnimPair(): Pair<Int, Int> {
            when(this) {
                SLIDE -> return Pair(R.anim.slide_left, R.anim.slide_right)
                FADE -> return Pair(R.anim.fade_in, R.anim.fade_out)
            }

            return Pair(R.anim.slide_left, R.anim.slide_right)
        }
    }
}