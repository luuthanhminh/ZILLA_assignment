package com.zilla.android.ui.category

import com.zilla.android.models.Category
import com.zilla.android.ui.base.BaseContract

class CategoryContract {

    interface View: BaseContract.View {
        fun showProgress(show: Boolean)
        fun getCategoriesSuccess(list: List<Category>)
        fun getCategoriesFail(error: Throwable)
    }

    interface Presenter: BaseContract.Presenter<CategoryContract.View> {
        fun onGetCategories()
    }
}