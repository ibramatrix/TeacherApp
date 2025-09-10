package com.helloworld.universalschoolteacher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.helloworld.universalschoolteacher.R
import com.helloworld.universalschoolteacher.model.Diary

class DiaryAdapter(
    private val context: Context,
    private val diaries: MutableList<Pair<String, Diary>>,
    private val onEdit: (id: String, diary: Diary) -> Unit,
    private val onDelete: (id: String) -> Unit
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDiary: TextView = view.findViewById(R.id.textDiary)
        val dateDiary: TextView = view.findViewById(R.id.dateDiary)
        val imageDiary: ImageView = view.findViewById(R.id.imageDiary)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_diary, parent, false)
        return DiaryViewHolder(view)
    }

    override fun getItemCount(): Int = diaries.size

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val (id, diary) = diaries[position]
        holder.textDiary.text = diary.Text
        holder.dateDiary.text = diary.Date
        val images = diary.Images
        if (!images.isNullOrEmpty()) {
            Glide.with(context).load(images[0]).into(holder.imageDiary)
        }

        holder.btnEdit.setOnClickListener {
            onEdit(id, diary)
        }
        holder.btnDelete.setOnClickListener {
            onDelete(id)
        }
    }
}
