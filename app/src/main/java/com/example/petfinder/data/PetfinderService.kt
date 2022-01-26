package com.example.petfinder.data

import io.reactivex.Observable
import retrofit2.http.*

data class TokenReqBody(
    val grant_type: String,
    val client_id: String,
    val client_secret: String
)

interface PetfinderService {

    @POST("oauth2/token")
    fun getToken(@Body body: TokenReqBody): Observable<TokenResponce>

    @GET("types")
    fun getTypes(): Observable<TypesResponce>

    @GET("types/{type}/breeds")
    fun getBreedsByType(@Path("type") type: String): Observable<BreedsResponse>

    @GET("animals")
    fun getAnimals(
        @Query("type") type: String,
        @Query("breed") breed: String
    ): Observable<AnimalsResponse>
}
