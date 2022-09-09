package su.pank.ilovedogs

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import su.pank.ilovedogs.models.Breed
import su.pank.ilovedogs.models.Dog
import su.pank.ilovedogs.models.LikedDog
import su.pank.ilovedogs.ui.theme.ILoveDogsTheme
import java.io.FileNotFoundException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ILoveDogsTheme {
                LaunchedEffect(null) {

                    var likes = JSONArray()
                    try {
                        likes = JSONArray(
                            baseContext.openFileInput("likes.json").bufferedReader().readText()
                        )
                    } catch (e: FileNotFoundException) {
                        baseContext.openFileOutput("likes.json", Context.MODE_PRIVATE).use {
                            it.write("[]".toByteArray())
                        }
                    }
                    DogsAppContext.likes = Gson().fromJson(likes.toString(), Array<LikedDog>::class.java).toMutableList()
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ILoveDogsTheme {
        Navigation()
    }
}