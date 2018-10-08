package com.zilla.android.ui.category

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.Toast
import com.zilla.android.R
import com.zilla.android.di.component.DaggerActivityComponent
import com.zilla.android.di.module.ActivityModule
import com.zilla.android.models.Category
import com.zilla.android.ui.main.MainActivity
import com.zilla.android.ui.player.MusicPlayerActivity
import com.zilla.android.util.TimeUtils
import kotlinx.android.synthetic.main.activity_category.*
import javax.inject.Inject

class CategoryActivity: AppCompatActivity(), CategoryContract.View {



    @Inject
    lateinit var presenter: CategoryContract.Presenter


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // https://crazygui.wordpress.com/2010/09/05/high-quality-radial-gradient-in-android/
        val displayMetrics = resources.displayMetrics
        // int screenWidth = displayMetrics.widthPixels;
        val screenHeight = displayMetrics.heightPixels

        val window = window
        val gradientBackgroundDrawable = TimeUtils.create(
                ContextCompat.getColor(this, R.color.dark_blue_gradientColor),
                ContextCompat.getColor(this, R.color.dark_blue_background),
                screenHeight / 2, // (int) Math.hypot(screenWidth / 2, screenHeight / 2),
                0.5f,
                0.5f
        )
        window.setBackgroundDrawable(gradientBackgroundDrawable)
        window.setFormat(PixelFormat.RGBA_8888)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        injectDependency()

        presenter.attach(this)
        presenter.subscribe()
        presenter.onGetCategories()
    }

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .build()
        activityComponent.inject(this)
    }


    override fun showProgress(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    override fun getCategoriesSuccess(list: List<Category>) {
        var adapter = ListCategoryAdapter(this@CategoryActivity, list.toMutableList(), object : ListCategoryAdapter.OnItemClickListener{
            override fun onItemClick(category: Category) {
                var intent = Intent(this@CategoryActivity, MusicPlayerActivity::class.java)
                intent.putExtra("data", category)
                startActivity(intent)
            }
        })
        recyclerView.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this@CategoryActivity)
        recyclerView!!.adapter = adapter
        adapter.notifyDataSetChanged();

    }

    override fun getCategoriesFail(error: Throwable) {
        Toast.makeText(this@CategoryActivity, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }





}