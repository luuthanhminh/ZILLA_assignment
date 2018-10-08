package com.zilla.android.models

import com.google.gson.annotations.SerializedName
import com.zilla.android.ui.player.PlayMode
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class PlayList constructor() :  Serializable {

    val NO_POSITION = -1

    private var playMode = PlayMode.LOOP

    private var songs:  MutableList<Song>? = mutableListOf()

    private var playingIndex:  Int = 0

    var numOfSongs: Int = 0

    constructor (song: Song) : this() {
        songs!!.add(song)
        numOfSongs = 1
    }



    fun getSongs(): MutableList<Song> {
        if (songs == null) {
            songs = mutableListOf()
        }
        return songs!!
    }

    fun setSongs(songs: MutableList<Song>?) {
        var songs = songs
        if (songs == null) {
            songs = mutableListOf()
        }
        this.songs = songs
    }


    fun getPlayingIndex(): Int {
        return playingIndex
    }

    fun setPlayingIndex(playingIndex: Int) {
        this.playingIndex = playingIndex
    }

    fun getPlayMode(): PlayMode {
        return playMode
    }

    fun setPlayMode(playMode: PlayMode) {
        this.playMode = playMode
    }


    fun getItemCount(): Int {
        return if (songs == null) 0 else songs!!.size
    }

    fun addSong(song: Song?) {
        if (song == null) return

        songs!!.add(song)
        numOfSongs = songs!!.size
    }

    fun addSong(song: Song?, index: Int) {
        if (song == null) return

        songs!!.add(index, song)
        numOfSongs = songs!!.size
    }

    fun addSong(songs: List<Song>?, index: Int) {
        if (songs == null || songs.isEmpty()) return

        this.songs!!.addAll(index, songs)
        this.numOfSongs = this.songs!!.size
    }

    fun removeSong(song: Song?): Boolean {
        if (song == null) return false

        val index: Int = songs!!.indexOf(song!!)
        if (index != -1) {
            if (songs!!.removeAt(index) != null) {
                numOfSongs = songs!!.size
                return true
            }
        } else {
            val iterator = songs!!.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (song.path.equals(item.path)) {
                    iterator.remove()
                    numOfSongs = songs!!.size
                    return true
                }
            }
        }
        return false
    }

    /**
     * Prepare to play
     */
    fun prepare(): Boolean {
        if (songs!!.isEmpty()) return false
        if (playingIndex == NO_POSITION) {
            playingIndex = 0
        }
        return true
    }

    /**
     * The current song being played or is playing based on the [.playingIndex]
     */
    fun getCurrentSong(): Song? {
        return if (playingIndex != NO_POSITION) {
            songs!![playingIndex]
        } else null
    }

    fun hasLast(): Boolean {
        return songs != null && songs!!.size != 0
    }

    fun last(): Song {
        when (playMode) {
            PlayMode.LOOP, PlayMode.LIST, PlayMode.SINGLE -> {
                var newIndex = playingIndex - 1
                if (newIndex < 0) {
                    newIndex = songs!!.size - 1
                }
                playingIndex = newIndex
            }
            PlayMode.SHUFFLE -> playingIndex = randomPlayIndex()
        }
        return songs!![playingIndex]
    }

    fun hasNext(fromComplete: Boolean): Boolean {
        if (songs!!.isEmpty()) return false
        if (fromComplete) {
            if (playMode === PlayMode.LIST && playingIndex + 1 >= songs!!.size) return false
        }
        return true
    }

    operator fun next(): Song {
        when (playMode) {
            PlayMode.LOOP, PlayMode.LIST, PlayMode.SINGLE -> {
                var newIndex = playingIndex + 1
                if (newIndex >= songs!!.size) {
                    newIndex = 0
                }
                playingIndex = newIndex
            }
            PlayMode.SHUFFLE -> playingIndex = randomPlayIndex()
        }
        return songs!![playingIndex]
    }

    private fun randomPlayIndex(): Int {
        val randomIndex = Random().nextInt(songs!!.size)
        // Make sure not play the same song twice if there are at least 2 songs
        if (songs!!.size > 1 && randomIndex == playingIndex) {
            randomPlayIndex()
        }
        return randomIndex
    }


}