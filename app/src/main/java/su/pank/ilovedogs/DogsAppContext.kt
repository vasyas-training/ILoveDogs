package su.pank.ilovedogs

import androidx.compose.runtime.mutableStateOf
import su.pank.ilovedogs.models.Breed
import su.pank.ilovedogs.models.LikedDog

// Контекст приложения, который представляет собой статический класс
class DogsAppContext {
    companion object {
        var isError = mutableStateOf(false)
        lateinit var likes: MutableList<LikedDog>
        lateinit var imageNowShowing: String
        var breeds = mutableListOf<Breed>()
    }
}