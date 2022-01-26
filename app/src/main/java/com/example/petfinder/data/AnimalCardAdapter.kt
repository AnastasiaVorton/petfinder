package com.example.petfinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petfinder.R

class AnimalCardAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var animalsList = listOf<AnimalDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AnimalCardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.animal_card, parent, false),
            parent.context
        )
    }

    override fun getItemCount(): Int = animalsList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val movieViewHolder = viewHolder as AnimalCardViewHolder
        movieViewHolder.bindView(animalsList[position])
    }

    fun setAnimalList(listOfBooks: List<AnimalDto>) {
        this.animalsList = listOfBooks
        notifyDataSetChanged()
    }
}