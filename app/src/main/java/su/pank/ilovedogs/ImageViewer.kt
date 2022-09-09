package su.pank.ilovedogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.Theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import su.pank.ilovedogs.api.getDogImages
import su.pank.ilovedogs.models.Dog
import su.pank.ilovedogs.ui.theme.ILoveDogsTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import su.pank.ilovedogs.models.LikedDog

// Породы перестали быть классами, потому что они не доделаны)
@OptIn(ExperimentalPagerApi::class)
@SuppressLint(
    "MutableCollectionMutableState", "CoroutineCreationDuringComposition",
    "UnusedMaterialScaffoldPaddingParameter"
)
@Composable
fun ImageViewer(breed: String, subBreed: String? = null) {
    println("$breed $subBreed")
    var images by remember {
        mutableStateOf(mutableListOf<String>())
    }
    var isLiked by remember {
        mutableStateOf(false)
    }
    val dog = Dog(breed, images, subBreed)
    CoroutineScope(Dispatchers.IO).launch {
        images = getDogImages(dog)
    }
    val pagerState = rememberPagerState()


    if (images.isNotEmpty())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                count = dog.imageUrls.size,
                state = pagerState,
                modifier = Modifier.weight(1F)
            ) {

                    page ->

                val painter = rememberAsyncImagePainter(dog.imageUrls[page])
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = painter.state is AsyncImagePainter.State.Loading,
                            color = Color.Gray,
                            highlight = PlaceholderHighlight.shimmer(
                                Color.Gray
                            )
                        )
                )
            }

            if (images.isNotEmpty()) {
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        println(page)
                        isLiked = LikedDog(
                            breed + " " + (subBreed ?: ""),
                            images[page]
                        ) in DogsAppContext.likes
                    }
                }
                val context = LocalContext.current

                FloatingActionButton(
                    onClick = {
                        isLiked = LikedDog(
                            breed + " " + (subBreed ?: ""),
                            images[pagerState.currentPage]
                        ) in DogsAppContext.likes
                        if (isLiked) {
                            isLiked = false
                            DogsAppContext.likes.remove(
                                LikedDog(
                                    breed + " " + (subBreed ?: ""),
                                    images[pagerState.currentPage]
                                )
                            )
                        }
                        else {
                            isLiked = true
                            DogsAppContext.likes.add(
                                LikedDog(
                                    breed + " " + (subBreed ?: ""),
                                    images[pagerState.currentPage]
                                )!!
                            )
                        }
                        if (DogsAppContext.likes.isNotEmpty())
                            println(DogsAppContext.likes[0])
                        CoroutineScope(Dispatchers.IO).launch {
                            context.openFileOutput("likes.json", Context.MODE_PRIVATE).use {
                                it.write(
                                    Gson().toJson(DogsAppContext.likes.toTypedArray()).toByteArray()
                                )
                            }
                        }
                    },
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(10.dp)
                ) {
                    Icon(
                        if (isLiked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null
                    )
                }
            }


        }

}