package su.pank.ilovedogs

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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

// Породы перестали быть классами, потому что они не доделаны)
@OptIn(ExperimentalPagerApi::class)
@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition",
    "UnusedMaterialScaffoldPaddingParameter"
)
@Composable
fun ImageViewer(breed: String, subBreed: String? = null){
    println("$breed $subBreed")
    var images by remember {
        mutableStateOf(mutableListOf<String>())
    }
    val dog = Dog(breed, images, subBreed)
    println(dog)

    CoroutineScope(Dispatchers.IO).launch{
        images = getDogImages(dog)
    }
    val pagerState = rememberPagerState()

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        HorizontalPager(count = dog.imageUrls.size, state = pagerState, modifier = Modifier.weight(1F)) {

                page -> val painter = rememberAsyncImagePainter(dog.imageUrls[page])
            Image(painter = painter, contentDescription = null, contentScale = ContentScale.FillWidth, modifier = Modifier
                .fillMaxWidth()
                .placeholder(
                    visible = painter.state is AsyncImagePainter.State.Loading,
                    color = Color.Gray,
                    highlight = PlaceholderHighlight.shimmer(
                        Color.Gray
                    )
                ))
        }
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                }
            },
            content = {PaddingValues(0.dp)},
            modifier = Modifier.weight(0.13F)
        )
    }

}