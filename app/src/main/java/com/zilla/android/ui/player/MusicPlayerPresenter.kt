package com.zilla.android.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.zilla.android.api.ApiServiceInterface
import com.zilla.android.models.Category
import com.zilla.android.models.Song
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MusicPlayerPresenter constructor() : MusicPlayerContract.Presenter {


    override fun onGetSongByPlayList(category: Category) {
        var subscribe = api.getSongByPlayList(category.id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list: List<Song>? ->
                    view.showProgress(false)
                    view.getSongByPlayListSuccess(list!!)
                }, { error ->
                    view.showProgress(false)
                    view.getSongByPlayListFail(error)
                })
        subscriptions.add(subscribe)
    }


    constructor (context: Context) : this() {
        this.context = context
    }


    private val subscriptions = CompositeDisposable()
    private lateinit var view: MusicPlayerContract.View
    private lateinit var context: Context
    private val api: ApiServiceInterface = ApiServiceInterface.create()

    private var mPlaybackService: PlaybackService? = null
    private var mIsServiceBound: Boolean = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mPlaybackService = (service as PlaybackService.LocalBinder).service
            view!!.onPlaybackServiceBound(mPlaybackService!!)
            //view!!.onSongUpdated(mPlaybackService!!.playingSong)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mPlaybackService = null
            view!!.onPlaybackServiceUnbound()
        }
    }

    override fun subscribe() {

        bindPlaybackService()
        retrieveLastPlayMode()

        // TODO
        if (mPlaybackService != null && mPlaybackService!!.isPlaying) {
            view!!.onSongUpdated(mPlaybackService!!.playingSong)
        } else {
            // - load last play list/folder/song
        }
    }

    override fun unsubscribe() {
        unbindPlaybackService()
        subscriptions.clear()
    }

    override fun attach(view: MusicPlayerContract.View) {
        this.view = view
    }




    override fun retrieveLastPlayMode() {
        view!!.updatePlayMode(PlayMode.SINGLE)
    }


    override fun bindPlaybackService() {
        context!!.bindService(Intent(context, PlaybackService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        mIsServiceBound = true
    }

    override fun unbindPlaybackService() {
        if (mIsServiceBound) {
            context!!.unbindService(mConnection)
            mIsServiceBound = false
        }
    }
}