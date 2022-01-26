package com.example.petfinder.data

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfinder.AnimalDetailsActivity
import com.example.petfinder.R
import kotlinx.android.synthetic.main.animal_card.view.*

class AnimalCardViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

    fun bindView(animal: AnimalDto) {
        if (animal.photoUrl == "")
            itemView.imageView.setBackgroundResource(R.drawable.default_image)
        else
            Glide.with(itemView.context)
                .load(animal.photoUrl)
                .into(itemView.imageView)

        itemView.breed.text = animal.breed
        itemView.animalName.text = animal.name
        itemView.description.text = animal.description
        itemView.age.text = animal.age
        itemView.setOnClickListener {
            val finishIntent = Intent(
                context,
                AnimalDetailsActivity::class.java).apply {
                putExtra("photo", animal.photoUrl)
                putExtra("type", animal.type)
                putExtra("size", animal.size)
                putExtra("age", animal.age)
                putExtra("gender", animal.gender)
                putExtra("description", animal.description)
            }
            finishIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(finishIntent)
        }
    }

}