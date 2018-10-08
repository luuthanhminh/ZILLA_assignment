package com.zilla.android.ui.player

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.zilla.android.R
import com.zilla.android.models.Category
import com.zilla.android.models.Song

class ListSongAdapter(private val context: Context, private val list: MutableList<Song>,
                      private val callback: ListSongAdapter.OnItemClickListener) : RecyclerView.Adapter<ListSongAdapter.ListViewHolder>() {

    private val listener: ListSongAdapter.OnItemClickListener = callback

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false)
        return ListSongAdapter.ListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder?, position: Int) {
        var item = list[position]

        holder!!.tvTitle!!.setText(item.displayName)
        holder!!.tvInfo!!.setText(item.artist)
        holder!!.tvDate!!.setText(item.releaseTimestamp.toString())

        holder.rlRoot!!.setOnClickListener {
            listener.onItemClick(item)
        }
    }


    class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val rlRoot = itemView.findViewById<RelativeLayout>(R.id.item_song_rl_root)
        val tvTitle = itemView.findViewById<TextView>(R.id.text_view_name)
        val tvInfo = itemView.findViewById<TextView>(R.id.text_view_info)
        val tvDate = itemView.findViewById<TextView>(R.id.text_view_date)
    }

    interface OnItemClickListener {
        fun onItemClick(song: Song)
    }
}