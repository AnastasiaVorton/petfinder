package com.example.petfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_animal_details.*
import kotlinx.android.synthetic.main.animal_card.view.*

class AnimalDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_details)

        detailsAge.text = "Age: ${intent.getStringExtra("age")}"
        detailsDescription.text = intent.getStringExtra("description")
        detailsGender.text = "Gender: ${intent.getStringExtra("gender")}"
        detailsSize.text = "Size: ${intent.getStringExtra("age")}"
        detailsType.text = intent.getStringExtra("type")

        if (intent.getStringExtra("photo") == "") {
            detailsPhoto.setBackgroundResource(R.drawable.default_image)
        } else
            Glide.with(this)
                .load(intent.getStringExtra("photo"))
                .into(detailsPhoto)
    }
}
