package com.zilla.android.ui.player

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import com.zilla.android.R
import com.zilla.android.di.component.DaggerActivityComponent
import com.zilla.android.di.module.ActivityModule
import com.zilla.android.event.PlayListNowEvent
import com.zilla.android.event.PlaySongEvent
import com.zilla.android.models.Category
import com.zilla.android.models.PlayList
import com.zilla.android.models.Song
import com.zilla.android.ui.main.MainContract
import com.zilla.android.util.TimeUtils
import kotlinx.android.synthetic.main.activity_music_player.*
import javax.inject.Inject
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.EventBus
import android.graphics.Bitmap
import android.R.attr.path
import android.app.Dialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.zilla.android.ui.category.ListCategoryAdapter
import com.zilla.android.ui.widget.ProgressDialog
import kotlinx.android.synthetic.main.activity_category.*


class MusicPlayerActivity: AppCompatActivity(), MusicPlayerContract.View, IPlayback.Callback {

    private var dialog: Dialog? = null

    override fun showProgress(show: Boolean) {
        if(dialog == null){
            dialog = ProgressDialog.progressDialog(this@MusicPlayerActivity)
        }
        if(show)
            dialog!!.show()
        else
            dialog!!.hide()
    }

    override fun getSongByPlayListSuccess(list: List<Song>) {
        var adapter = ListSongAdapter(this@MusicPlayerActivity, list.toMutableList(), object : ListSongAdapter.OnItemClickListener{
            override fun onItemClick(song: Song) {
                radioButtonShowPlayList.performClick()
                EventBus.getDefault().post(PlaySongEvent(song))
            }
        })
        recyclerViewPlayList.setHasFixedSize(true)
        recyclerViewPlayList!!.layoutManager = LinearLayoutManager(this@MusicPlayerActivity)
        recyclerViewPlayList!!.adapter = adapter
        adapter.notifyDataSetChanged()

        var playList = PlayList()
        playList.addSong(list, 0)
        EventBus.getDefault().post(PlayListNowEvent(playList, 0))
    }

    override fun getSongByPlayListFail(error: Throwable) {
        Toast.makeText(this@MusicPlayerActivity, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onSwitchLast(last: Song?) {
        onSongUpdated(last)
    }

    override fun onSwitchNext(next: Song?) {
        onSongUpdated(next)
    }

    override fun onComplete(next: Song?) {
        onSongUpdated(next)
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {

        updatePlayToggle(isPlaying)
        if (isPlaying) {
            imageViewAlbum.resumeRotateAnimation()
            mHandler.removeCallbacks(mProgressCallback)
            mHandler.post(mProgressCallback)
        } else {
            imageViewAlbum.pauseRotateAnimation()
            mHandler.removeCallbacks(mProgressCallback)
        }
    }

    override fun handleError(error: Throwable) {
        Toast.makeText(this@MusicPlayerActivity, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onPlaybackServiceBound(service: PlaybackService) {
        mPlayer = service
        mPlayer!!.registerCallback(this)
    }

    override fun onPlaybackServiceUnbound() {
        mPlayer!!.unregisterCallback(this)
        mPlayer = null
    }

    override fun onSongUpdated(song: Song?) {
        if (song == null) {
            imageViewAlbum.cancelRotateAnimation()
            buttonPlayToggle.setImageResource(R.drawable.ic_play)
            seekBarProgress.progress = 0
            updateProgressTextWithProgress(0)
            seekTo(0)
            mHandler.removeCallbacks(mProgressCallback)
            return
        }
        // Step 1: Song name and artist
        textViewName.text = song.displayName
        textViewArtist.text = song.artist
        // Step 2: favorite
        buttonFavoriteToggle.setImageResource(R.drawable.ic_favorite_no)
        // Step 3: Duration
        textViewDuration.text = TimeUtils.formatDuration(song.duration)
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        imageViewAlbum.setImageResource(R.drawable.default_record_album)

        Glide.with(this)
                .load(song.artworkUrl)
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                        imageViewAlbum.setImageBitmap(TimeUtils.getCroppedBitmap(resource))
                    }
                })

        imageViewAlbum.pauseRotateAnimation()
        mHandler.removeCallbacks(mProgressCallback)
        if (mPlayer!!.isPlaying) {
            imageViewAlbum.startRotateAnimation()
            mHandler.post(mProgressCallback)
            buttonPlayToggle.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun updatePlayMode(playMode: PlayMode) {
        var playMode = playMode
        if (playMode == null) {
            playMode = PlayMode.LOOP
        }
        when (playMode) {
            PlayMode.LIST -> buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_list)
            PlayMode.LOOP -> buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_loop)
            PlayMode.SHUFFLE -> buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_shuffle)
            PlayMode.SINGLE -> buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_single)
        }
    }

    override fun updatePlayToggle(play: Boolean) {
        buttonPlayToggle.setImageResource(if (play) R.drawable.ic_pause else R.drawable.ic_play)
    }

    override fun updateFavoriteToggle(favorite: Boolean) {
        buttonFavoriteToggle.setImageResource(if (favorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
    }

    @Inject
    lateinit var presenter: MusicPlayerContract.Presenter

    private var mPlayer: IPlayback? = null

    private val mHandler = Handler()

    private val UPDATE_PROGRESS_INTERVAL: Long = 1000

    private val mProgressCallback = object : Runnable {
        override fun run() {
            if (isFinishing) return

            if (mPlayer!!.isPlaying) {
                val progress = (seekBarProgress.max * ((mPlayer!!.progress.toFloat() / mPlayer!!.duration.toFloat()))).toInt()
                updateProgressTextWithDuration(mPlayer!!.progress)
                updateTextWithDuration(mPlayer!!.duration)
                if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBarProgress.setProgress(progress, true)
                    } else {
                        seekBarProgress.progress = progress
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
        ButterKnife.bind(this)
        injectDependency()

        presenter.attach(this)
        presenter.subscribe()

        var category = intent.getSerializableExtra("data") as Category
        presenter.onGetSongByPlayList(category)

        radioButtonShowPlayList.setOnClickListener {
            llPLayList.visibility = if(llPLayList.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mHandler.removeCallbacks(mProgressCallback)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekTo(getDuration(seekBar.progress))
                if (mPlayer!!.isPlaying) {
                    mHandler.removeCallbacks(mProgressCallback)
                    mHandler.post(mProgressCallback)
                }
            }
        })
        buttonPlayToggle.setOnClickListener(View.OnClickListener {
            if (mPlayer == null) return@OnClickListener

            if (mPlayer!!.isPlaying) {
                mPlayer!!.pause()
            } else {
                mPlayer!!.play()
            }
        })
        buttonPlayModeToggle.setOnClickListener(View.OnClickListener {
            if (mPlayer == null) return@OnClickListener
            val newMode = PlayMode.switchNextMode(PlayMode.SINGLE)
            mPlayer!!.setPlayMode(newMode)
            updatePlayMode(newMode)
        })
        buttonPlayLast.setOnClickListener(View.OnClickListener {
            if (mPlayer == null) return@OnClickListener

            mPlayer!!.playLast()
        })
        buttonPlayNext.setOnClickListener(View.OnClickListener {
            if (mPlayer == null) return@OnClickListener

            mPlayer!!.playNext()
        })
        buttonFavoriteToggle.setOnClickListener(View.OnClickListener { view ->
            if (mPlayer == null) return@OnClickListener

            val currentSong = mPlayer!!.playingSong
            if (currentSong != null) {
                view!!.isEnabled = false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mHandler.removeCallbacks(mProgressCallback)
            mHandler.post(mProgressCallback)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        mHandler.removeCallbacks(mProgressCallback)
    }



    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onPlayListNowEvent(event: PlayListNowEvent) {
        val playList = event.playList
        val playIndex = event.playIndex
        playSong(playList, playIndex)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onPlaySongEvent(event: PlaySongEvent) {
        val song = event.song
        playSong(song)
    }


    private fun playSong(song: Song) {
        val playList = PlayList(song)
        playSong(playList, 0)
    }

    private fun playSong(playList: PlayList?, playIndex: Int) {
        if (playList == null) return

        playList!!.setPlayMode(PlayMode.SINGLE)
        mPlayer!!.play(playList, playIndex)

        val song = playList!!.getCurrentSong()
        onSongUpdated(song)
    }

    private fun updateProgressTextWithProgress(progress: Int) {
        val targetDuration = getDuration(progress)
        textViewProgress.text = TimeUtils.formatDuration(targetDuration)
    }

    private fun updateProgressTextWithDuration(duration: Int) {
        textViewProgress.text = TimeUtils.formatDuration(duration)
    }

    private fun updateTextWithDuration(duration: Int) {
        textViewDuration.text = TimeUtils.formatDuration(duration)
    }

    private fun seekTo(duration: Int) {
        mPlayer!!.seekTo(duration)
    }

    private fun getDuration(progress: Int): Int {
        return (mPlayer!!.duration * (progress.toFloat() / seekBarProgress.max)).toInt()
    }


    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .build()
        activityComponent.inject(this)
    }

}