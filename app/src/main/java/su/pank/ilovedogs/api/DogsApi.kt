package su.pank.ilovedogs.api


import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import su.pank.ilovedogs.models.Breed
import su.pank.ilovedogs.models.Dog

val retrofit = Retrofit.Builder().baseUrl("https://dog.ceo/api/").build()

// Отказываюсь от fuel из-за его проблем со стабильностью и контекста
interface DogsApi {
    @GET("breeds/list/all")
    suspend fun getBreeds(): Response<ResponseBody>

    @GET("breed/{breed}/images/random")
    suspend fun getRandomByBreed(@Path("breed") breedName: String): Response<ResponseBody>

    @GET("breed/{breed}/{subBreed}/images/random")
    suspend fun getRandomBySubBreed(@Path("breed") breedName: String, @Path("subBreed") subBreedName: String): Response<ResponseBody>
}

suspend fun getBreeds(): MutableList<Breed> {
    val breeds = mutableListOf<Breed>()
    val response = retrofit.create(DogsApi::class.java).getBreeds()
    if (response.code() != 200)
        throw Exception()
    val result = response.body()?.string()!!
    val breedsJSON = JSONObject(result).getJSONObject("message")
    for (key in breedsJSON.keys()) {
        val breed = Breed(key)
        val subBreedJSONArray = breedsJSON.getJSONArray(key)
        if (subBreedJSONArray.length() > 0) {
            val subBreeds = mutableListOf<Breed>()
            for (i in 0 until subBreedJSONArray.length()) {
                subBreeds.add(Breed(subBreedJSONArray.getString(i)))
            }
            breed.subBreeds = subBreeds
        }
        breeds.add(breed)
    }
    return breeds
}

fun getDogs(breed: Breed): List<Dog> {
    TODO()
}

suspend fun getRandomByBreed(breedName: String): String{
    val response = retrofit.create(DogsApi::class.java).getRandomByBreed(breedName)
    if (response.code() != 200)
        throw Exception()
    return JSONObject(response.body()!!.string()).getString("message")
}

suspend fun getRandomByBreed(breed: Breed): String{
    return getRandomByBreed(breed.name)
}

suspend fun getRandomBySubBreed(breedName: String, subBreedName: String): String{
    val response = retrofit.create(DogsApi::class.java).getRandomBySubBreed(breedName, subBreedName)
    if (response.code() != 200)
        throw Exception()
    return JSONObject(response.body()!!.string()).getString("message")
}