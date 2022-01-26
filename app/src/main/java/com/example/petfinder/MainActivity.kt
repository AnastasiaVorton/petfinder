package com.example.petfinder

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petfinder.data.*
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext


class MainActivity : AppCompatActivity() {

    lateinit var restClient: RestClient
    lateinit var sessionManager: SessionManager
    lateinit var typesArray: LiveData<List<String>>
    lateinit var breedsArray: LiveData<List<String>>
    lateinit var animalsArray: LiveData<List<AnimalDto>>
    lateinit var recyclerViewAdapter: AnimalCardAdapter
    lateinit var animalDao: AnimalDao


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        restClient = RestClient()
        recyclerViewAdapter = AnimalCardAdapter()

        animalDao = AnimalRoomDatabase.getDatabase(application).animalDao()

        getToken().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io()).subscribe { response ->
                sessionManager.saveAuthToken(response)
            }

        if (sessionManager.fetchAuthToken() !== null) {
            val x = sessionManager.fetchAuthToken()
            getTypes().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io()).subscribe { liveData ->
                    typesArray = liveData
                    liveData.observe(
                        this,
                        Observer { collection ->
                            val typesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, collection!!.toTypedArray())
                            typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerType!!.adapter = typesAdapter
                        }
                    )
                }



            spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    getBreeds().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io()).subscribe { liveData ->
                            breedsArray = liveData
                            liveData.observe(
                                this@MainActivity,
                                Observer { collection ->
                                    val breedsAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, collection!!.toTypedArray())
                                    breedsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinnerBreed!!.adapter = breedsAdapter
                                }
                            )
                        }
                }

            }

            spinnerBreed.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    getContentByBreed().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io()).subscribe { liveData ->
                            animalsArray = liveData
                            liveData.observe(
                                this@MainActivity,
                                Observer { collection ->
                                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity);
                                    recyclerView.adapter = recyclerViewAdapter
                                    (recyclerView.adapter as AnimalCardAdapter).setAnimalList(collection)
                                })
                        }
                }

            }
        }

    }

    private fun getContentByBreed(): Observable<LiveData<List<AnimalDto>>> {
        return restClient.getApiService(this).getAnimals(
            spinnerType.selectedItem.toString().toLowerCase(),
            spinnerBreed.selectedItem.toString().toLowerCase())
            .map { response: AnimalsResponse ->
                val animals = response.animalsArray.take(25).map { animal -> mapAnimalToAnimalDto(animal) } ?: listOf<AnimalDto>()
                CoroutineScope(EmptyCoroutineContext).launch { insertAnimals(animals)}
                MutableLiveData(animals) as LiveData<List<AnimalDto>>}
            .onErrorReturn {error ->
                Log.e(error.message, error.localizedMessage)
                animalDao.getAnimalsByTypeAndBreed(
                    spinnerType.selectedItem.toString().toLowerCase(),
                    spinnerBreed.selectedItem.toString().toLowerCase())
            }
    }

    private fun getBreeds(): Observable<LiveData<List<String>>> {
        return restClient.getApiService(this).getBreedsByType(spinnerType.selectedItem.toString().toLowerCase())
            .map { response ->
                val breeds = response.breedsArray.take(100).map { type -> type.name }
                MutableLiveData(breeds) as LiveData<List<String>>}
            .onErrorReturn {error ->
                Log.e(error.message, error.localizedMessage)
                animalDao.getBreedsForType(spinnerType.selectedItem.toString().toLowerCase())
            }
    }

    private fun getToken(): Observable<String> {
        return restClient.getApiService(this).getToken(TokenReqBody("client_credentials", BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET))
            .map { response: TokenResponce -> response.token }
            .onErrorReturn {error ->
                Log.e(error.message, error.localizedMessage)
                return@onErrorReturn ""
            }
    }

    private fun getTypes(): Observable<LiveData<List<String>>> {
        return restClient.getApiService(this).getTypes()
            .map { response: TypesResponce? ->
                Log.e("z", response.toString())
                val types = response?.typesArray!!.map { type -> type.name }.toList()
                MutableLiveData(types) as LiveData<List<String>>
            }
            .onErrorReturn {error ->
                Log.e(error.message, error.localizedMessage)
                animalDao.getTypes()
            }
    }

    private fun mapAnimalToAnimalDto(animal: Animal): AnimalDto =
        AnimalDto(
            id = animal.id,
            type = animal.type,
            breed = animal.breed.breed,
            age = animal.age,
            gender = animal.gender,
            size = animal.size,
            name = animal.name,
            description = animal.description ?: "",
            photoUrl = animal.photos.firstOrNull()?.photo ?: ""
        )

    @WorkerThread
    suspend fun insertAnimals(animals: List<AnimalDto>) {
        animalDao.insert(animals)
    }
}
