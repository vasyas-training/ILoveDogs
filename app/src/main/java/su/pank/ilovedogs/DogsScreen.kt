package su.pank.ilovedogs

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import su.pank.ilovedogs.api.getBreeds
import su.pank.ilovedogs.api.getRandomByBreed
import su.pank.ilovedogs.models.Breed
import java.util.*

val MY_VK_LOGO =
    "https://sun9-83.userapi.com/impg/ARDUrlmffYmGH0mkzkxZG6CEmitTGy6wskVstQ/4FRA5X2JLq4.jpg?size=960x1280&quality=95&sign=c139b9f7c65048eff8b8dcd84904145d&type=album"

@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@Composable
fun Dogs() {
    var breeds by remember {
        mutableStateOf(mutableListOf<Breed>())
    }

    if (breeds.isEmpty())
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Text("Информация загружается, пожалуйста подождите", textAlign = TextAlign.Center)
        }

    LazyColumn {

        try {

            CoroutineScope(Dispatchers.IO).launch {
                breeds = getBreeds().toMutableList()

            }

        } catch (e: Exception) {
            DogsAppContext.isError = true
        }
        items(breeds) { breed ->
            var logoUrl by remember {
                mutableStateOf(MY_VK_LOGO)
            }
            CoroutineScope(Dispatchers.IO).launch {
                logoUrl = getRandomByBreed(breed)
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(logoUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp).clip(RoundedCornerShape(12.dp))
                            .placeholder(
                                visible = logoUrl == MY_VK_LOGO,
                                highlight = PlaceholderHighlight.shimmer(),
                                color = Color.Gray
                            )
                    )
                    Text(
                        text = breed.name.capitalize(Locale.ROOT),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    if (breed.subBreed != null)
                        Text(text = " (${breed.subBreed!!.size} subbreeds)", fontSize = 16.sp)

                }

            }
        }
    }
}