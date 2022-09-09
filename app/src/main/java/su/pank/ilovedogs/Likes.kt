package su.pank.ilovedogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.google.gson.Gson
import org.json.JSONArray
import su.pank.ilovedogs.models.LikedDog
import java.util.*

@Composable
fun Likes(navController: NavController) {
    if (DogsAppContext.likes.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "In order for something to be here,\n like any dog.")
        }
    } else {
        val likesMap = mutableMapOf<String, List<LikedDog>>().apply {
            val keys = mutableSetOf<String>()
            DogsAppContext.likes.forEach { likedDog -> keys.add(likedDog.breedName) }
            for (key in keys) {
                this[key] = DogsAppContext.likes.filter { dog -> dog.breedName == key }.toList()
            }
        }
        LazyColumn {
            items(likesMap.keys.toTypedArray()) { key ->
                val likedDogs = likesMap[key]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            navController.navigate("likes_view/$key")
                        }
                ) {
                    val asyncImagePainter =
                        rememberAsyncImagePainter(likesMap[key]!!.random().imageUrl)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = asyncImagePainter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .placeholder(
                                    visible = asyncImagePainter.state is AsyncImagePainter.State.Loading,
                                    highlight = PlaceholderHighlight.shimmer(),
                                    color = Color.Gray
                                )
                        )
                        Text(
                            text = key.capitalize(Locale.ROOT),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(10.dp)
                        )

                    }
                }
            }
        }
    }
}