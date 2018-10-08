package com.zilla.android.ui.player

enum class PlayMode {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;


    companion object {

        fun switchNextMode(current: PlayMode?): PlayMode {
            if (current == null) return LOOP

            when (current) {
                LOOP -> return LIST
                LIST -> return SHUFFLE
                SHUFFLE -> return SINGLE
                SINGLE -> return LOOP
            }
            return LOOP
        }
    }
}