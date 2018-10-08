package com.zilla.android.ui.player

import com.zilla.android.models.Category
import com.zilla.android.models.Song
import com.zilla.android.ui.base.BaseContract



class MusicPlayerContract {

    interface View: BaseContract.View {

        fun showProgress(show: Boolean)

        fun getSongByPlayListSuccess(list: List<Song>)

        fun getSongByPlayListFail(error: Throwable)

        fun handleError(error: Throwable)

        fun onPlaybackServiceBound(service: PlaybackService)

        fun onPlaybackServiceUnbound()

        fun onSongUpdated(song: Song?)

        fun updatePlayMode(playMode: PlayMode)

        fun updatePlayToggle(play: Boolean)

        fun updateFavoriteToggle(favorite: Boolean)
    }

    interface Presenter: BaseContract.Presenter<MusicPlayerContract.View> {

        fun onGetSongByPlayList(category: Category)

        fun retrieveLastPlayMode()

        fun bindPlaybackService()

        fun unbindPlaybackService()
    }
}
