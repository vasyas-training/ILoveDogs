package su.pank.ilovedogs.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// Порода собаки, имеет функцию получения списка подпород, как строк
data class Breed(val name: String, var subBreeds: List<Breed>? = null, var logo: MutableState<String> = mutableStateOf("")){
    fun subBreedsToArray(): List<String>{
        return subBreeds?.map { breed ->  breed.name} as List<String>
    }
}
