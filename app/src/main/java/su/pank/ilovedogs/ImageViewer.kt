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
import androidx.compose.material.Text
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
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.gson.Gson
import su.pank.ilovedogs.models.LikedDog

// Породы перестали быть классами, потому что они не доделаны)
@OptIn(ExperimentalPagerApi::class)
@SuppressLint(
    "MutableCollectionMutableState", "CoroutineCreationDuringComposition",
    "UnusedMaterialScaffoldPaddingParameter"
)
@Composable
fun ImageViewerByBreed(breed: String, subBreed: String? = null) {
    var images by remember {
        mutableStateOf(mutableListOf<String>())
    }

    val dog = Dog(breed, images, subBreed)
    CoroutineScope(Dispatchers.IO).launch {
        images = getDogImages(dog)
    }

    if (images.isNotEmpty()) {
        ImageViewer(images = images, fullBreedName = breed + " " + (subBreed ?: ""))
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageViewer(images: List<String>, fullBreedName: String) {
    val pagerState = rememberPagerState()
    var isLiked by remember {
        mutableStateOf(false)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.weight(1F)
        ) { page ->
            val painter = rememberAsyncImagePainter(images[page])
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .placeholder(
                        visible = painter.state is AsyncImagePainter.State.Loading,
                        color = Color.Gray,
                        highlight = PlaceholderHighlight.shimmer(
                            Color.Gray
                        )
                    )
            )
        }
        if (images.size < 20)
            HorizontalPagerIndicator(
                pagerState = pagerState,
                inactiveColor = MaterialTheme.colorScheme.primaryContainer,
                activeColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 20.dp)
            )
        else
            Text(text = "${pagerState.currentPage + 1}/${images.size}")
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                DogsAppContext.imageNowShowing = images[page]
                isLiked = LikedDog(
                    fullBreedName,
                    images[page]
                ) in DogsAppContext.likes
            }
        }
        val context = LocalContext.current

        FloatingActionButton(
            onClick = {
                isLiked = LikedDog(
                    fullBreedName,
                    images[pagerState.currentPage]
                ) in DogsAppContext.likes
                if (isLiked) {
                    isLiked = false
                    DogsAppContext.likes.remove(
                        LikedDog(
                            fullBreedName,
                            images[pagerState.currentPage]
                        )
                    )
                } else {
                    isLiked = true
                    DogsAppContext.likes.add(
                        LikedDog(
                            fullBreedName,
                            images[pagerState.currentPage]
                        )
                    )
                }
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
                .padding(10.dp, 10.dp, 10.dp, 20.dp)
        ) {
            Icon(
                if (isLiked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null
            )
        }

    }

}
