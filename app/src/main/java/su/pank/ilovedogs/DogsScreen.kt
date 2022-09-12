package su.pank.ilovedogs

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import su.pank.ilovedogs.api.getBreeds
import su.pank.ilovedogs.api.getRandomByBreed
import su.pank.ilovedogs.api.getRandomBySubBreed
import su.pank.ilovedogs.models.Breed
import java.util.*

// Я пёс
// val MY_VK_LOGO = "https://sun9-83.userapi.com/impg/ARDUrlmffYmGH0mkzkxZG6CEmitTGy6wskVstQ/4FRA5X2JLq4.jpg?size=960x1280&quality=95&sign=c139b9f7c65048eff8b8dcd84904145d&type=album"

@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@Composable
fun Dogs(
    navController: NavController,
    breedsJSON: JSONArray = JSONArray(),
    breedParent: String = ""
) {
    val breedsNormal = if (DogsAppContext.breeds.isEmpty() || breedParent.isNotEmpty()) {
        mutableListOf()
    } else DogsAppContext.breeds
    if (breedsJSON.length() > 0) {
        for (i in 0 until breedsJSON.length())
            breedsNormal.add(Breed(breedsJSON.getString(i)))
    }

    var breeds by remember {
        mutableStateOf(breedsNormal)
    }

    if (breeds.isEmpty())
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Text(
                "Информация загружается,\n пожалуйста подождите...",
                textAlign = TextAlign.Center,
                softWrap = true
            )
        }

    val lazyListState = rememberLazyListState()
    if (breeds.isEmpty())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                breeds = getBreeds()
            } catch (e: Exception) {
                DogsAppContext.isError.value = true
            }
        }
    if (breeds.isNotEmpty()) {
        LazyColumn(state = lazyListState) {
            if (breedParent.isEmpty())
                DogsAppContext.breeds = breeds
            items(breeds, key = { it.name }) { breed ->
                if (!lazyListState.isScrollInProgress)
                    CoroutineScope(Dispatchers.IO).launch {
                        if (breed.logo.value.isEmpty())
                            try {
                                breed.logo.value =
                                    if (breedParent.isNotEmpty()) getRandomBySubBreed(
                                        breedParent,
                                        breed.name
                                    ) else
                                        getRandomByBreed(
                                            breed
                                        )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                DogsAppContext.isError.value = true
                            }
                    }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            if (breed.subBreeds == null)
                                navController.navigate("view/" + JSONArray().apply {
                                    if (breedParent == "")
                                        this.put(breed.name)
                                    else {
                                        this.put(breedParent)
                                        this.put(breed.name)
                                    }
                                })
                            else {
                                navController.navigate(
                                    "subBreeds/" + JSONObject()
                                        .apply {
                                            this.put("parent", breed.name)
                                            this.put(
                                                "subBreeds",
                                                JSONArray(
                                                    breed
                                                        .subBreedsToArray()
                                                        .toTypedArray()
                                                )
                                            )
                                        }
                                        .toString()
                                )
                            }
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val asyncImagePainter = rememberAsyncImagePainter(breed.logo.value)
                        var wasError by remember {
                            mutableStateOf(false)
                        }
                        if (asyncImagePainter.state is AsyncImagePainter.State.Error && wasError && breed.logo.value.isNotEmpty()) {
                            DogsAppContext.isError.value = true
                        } else if (asyncImagePainter.state is AsyncImagePainter.State.Error && breed.logo.value.isNotEmpty()) {
                            wasError = true
                        }
                        Image(
                            asyncImagePainter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .placeholder(
                                    visible = asyncImagePainter.state is AsyncImagePainter.State.Loading,
                                    highlight = PlaceholderHighlight.shimmer(),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                        )

                        Text(
                            text = breed.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            },
                            fontSize = 20.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                        if (breed.subBreeds != null)
                            Text(text = " (${breed.subBreeds!!.size} subbreeds)", fontSize = 16.sp)
                    }

                }

            }

        }
    }
}