package com.zilla.android.ui.category

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import com.zilla.android.R
import com.zilla.android.models.Category

class ListCategoryAdapter(private val context: Context, private val list: MutableList<Category>,
                          private val callback: ListCategoryAdapter.OnItemClickListener) : RecyclerView.Adapter<ListCategoryAdapter.ListViewHolder>() {

    private val listener: ListCategoryAdapter.OnItemClickListener = callback

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_category_layout, parent, false)
        return ListCategoryAdapter.ListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder?, position: Int) {
        var item = list[position]

        holder!!.tvTitle!!.setText(item.name)
        //holder.body!!.setText(post.body)

        holder.clLayout!!.setOnClickListener {
            listener.onItemClick(item)
        }
    }


    class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var clLayout = itemView.findViewById<ConstraintLayout>(R.id.item_category_layout)
        val ivAvatar = itemView.findViewById<ImageView>(R.id.item_category_iv_avatar)
        val tvTitle = itemView.findViewById<TextView>(R.id.item_category_tv_title)
    }

    interface OnItemClickListener {
        fun onItemClick(category: Category)
    }
}