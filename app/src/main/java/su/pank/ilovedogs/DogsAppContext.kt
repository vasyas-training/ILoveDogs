package su.pank.ilovedogs

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.json.JSONArray
import org.json.JSONObject
import su.pank.ilovedogs.models.Dog
import su.pank.ilovedogs.models.LikedDog

class DogsAppContext {
    companion object{
        var isError = false
        lateinit var likes: MutableList<LikedDog>
        lateinit var imageNowShowing: String
    }
}