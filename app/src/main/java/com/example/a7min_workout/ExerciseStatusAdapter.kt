package com.example.a7min_workout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7min_workout.databinding.ItemExerciseStatusBinding

class ExerciseStatusAdapter(val items: ArrayList<ExerciseModel>):
    RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemExerciseStatusBinding) :
        RecyclerView.ViewHolder(binding.root){
            val tvView = binding?.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExerciseStatusBinding.inflate(
            LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ExerciseModel = items[position]
        holder.tvView.text = model.getId().toString()

        when{
            model.getIsSelected() ->{
                holder.tvView.background =
                    ContextCompat.getDrawable(holder.itemView.context,
                        R.drawable.item_circular_thin_accent_border)
                holder.tvView.setTextColor(Color.parseColor("#212121"))
            }
            model.getIsCompleted() ->{
                holder.tvView.background =
                    ContextCompat.getDrawable(holder.itemView.context,
                        R.drawable.item_circular_color_accent_background)
                holder.tvView.setTextColor(Color.parseColor("#FFFFFF"))
            }
            else ->{
                holder.tvView.background =
                    ContextCompat.getDrawable(holder.itemView.context,
                        R.drawable.item_circular_gray_border)
                holder.tvView.setTextColor(Color.parseColor("#212121"))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}