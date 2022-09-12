package su.pank.ilovedogs

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import org.json.JSONArray
import su.pank.ilovedogs.models.LikedDog
import su.pank.ilovedogs.ui.theme.ILoveDogsTheme
import java.io.FileNotFoundException


fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
        .isConnected
}

// Activity, которое запускает навигацию и показывает ошибки
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
                    DogsAppContext.likes =
                        Gson().fromJson(likes.toString(), Array<LikedDog>::class.java)
                            .toMutableList()
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    Navigation(navController)

                    if (DogsAppContext.isError.value) {
                        AlertDialog(
                            onDismissRequest = { /* navController.popBackStack() */ },
                            text = {
                                Text(text = "Your internet connection is unstable or unavailable")
                            },
                            title = { Text(text = "Connection Error") },
                            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                            confirmButton = {
                                Button(onClick = {
                                    if (isNetworkAvailable(context)) {
                                        navController.popBackStack()
                                        DogsAppContext.isError.value = false
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Connection not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }) {
                                    Text("Retry Connection")
                                }
                            }
                        )
                    }
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