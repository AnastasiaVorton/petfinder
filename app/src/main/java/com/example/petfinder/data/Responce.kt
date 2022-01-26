package com.example.petfinder.data

import com.google.gson.annotations.SerializedName

data class TypesResponce(
    @SerializedName("types")
    val typesArray: Array<Type>
)

data class Type(
    @SerializedName("name")
    val name: String
)

data class TokenResponce(
    @SerializedName("access_token")
    val token: String
)

data class BreedsResponse(
    @SerializedName("breeds")
    val breedsArray: Array<Type>
)

data class AnimalsResponse(
    @SerializedName("animals")
    val animalsArray: Array<Animal>
)

data class Animal(
    @SerializedName("photos")
    val photos: Array<Photo>,

    @SerializedName("age")
    val age: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("breeds")
    val breed: Breed,

    @SerializedName("type")
    val type: String,

    @SerializedName("id")
    val id: Int,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("size")
    val size: String
)

data class Photo(
    @SerializedName("small")
    val photo: String
)

data class Breed(
    @SerializedName("primary")
    val breed: String
)