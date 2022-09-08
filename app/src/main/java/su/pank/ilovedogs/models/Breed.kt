package su.pank.ilovedogs.models


data class Breed(val name: String, var subBreeds: List<Breed>? = null, var logo: String? = null, var dogs: List<String>? = null){
    fun subBreedsToArray(): List<String>{
        return subBreeds?.map { breed ->  breed.name} as List<String>
    }
}
