package su.pank.ilovedogs

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import su.pank.ilovedogs.api.getDogImages
import su.pank.ilovedogs.models.Dog
import su.pank.ilovedogs.models.LikedDog

// Породы перестали быть классами, потому что они не доделаны)
//
// Данная функция позволяет получить список всех изображений по породе, то есть создать class Dog
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
        try {
            images = getDogImages(dog)
        } catch (e: Exception) {
            DogsAppContext.isError.value = true
        }
    }

    if (images.isNotEmpty()) {
        ImageViewer(images = images, fullBreedName = breed + " " + (subBreed ?: ""))
    }

}

// Функция которая обеспечивает просмотр изображений по списку их ссылок
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageViewer(images: List<String>, fullBreedName: String) {
    val pagerState = rememberPagerState()
    var isLiked by remember {
        mutableStateOf(false)
    }


    HorizontalPager(
        count = images.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val painter = rememberAsyncImagePainter(images[page])
        if (painter.state is AsyncImagePainter.State.Error)
            DogsAppContext.isError.value = true
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .placeholder(
                    visible = painter.state is AsyncImagePainter.State.Loading,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    highlight = PlaceholderHighlight.shimmer(
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
        )
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            DogsAppContext.imageNowShowing = images[page]
            isLiked = LikedDog(
                fullBreedName,
                images[page]
            ) in DogsAppContext.likes
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .zIndex(3f)
            .fillMaxSize()
    ) {
        if (images.size < 20)
            HorizontalPagerIndicator(
                pagerState = pagerState,
                inactiveColor = MaterialTheme.colorScheme.secondaryContainer,
                activeColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.shapes.medium
                )
            )
        else
            Text(
                text = "${pagerState.currentPage + 1}/${images.size}",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.shapes.medium
                    )
                    .width(60.dp),
                textAlign = TextAlign.Center
            )

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



