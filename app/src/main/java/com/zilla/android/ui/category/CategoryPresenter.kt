package com.zilla.android.ui.category

import com.zilla.android.api.ApiServiceInterface
import com.zilla.android.models.Category
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CategoryPresenter: CategoryContract.Presenter {


    private val subscriptions = CompositeDisposable()
    private val api: ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var view: CategoryContract.View

    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: CategoryContract.View) {
        this.view = view
    }

    override fun onGetCategories() {
        var subscribe = api.getCategories().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    view.showProgress(true)
                }
                .doOnTerminate {
                    view.showProgress(false)
                }
                .subscribe({ list: List<Category>? ->
                    view.getCategoriesSuccess(list!!)
                }, { error ->
                    view.getCategoriesFail(error)
                })
        subscriptions.add(subscribe)
    }


}