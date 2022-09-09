package su.pank.ilovedogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


fun getLocalBitmapUri(bmp: Bitmap, context: Context): Uri? {
    var bmpUri: Uri? = null
    try {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "share_image_" + System.currentTimeMillis() + ".png"
        )
        val out = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.close()
        bmpUri =
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bmpUri
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    val screens = listOf("Dogs", "Likes")
    var screenNow by remember {
        mutableStateOf(screens[0])
    }
    var canShare by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = screenNow.capitalize(Locale.ROOT)
            )
        }, navigationIcon = {
            if (screenNow !in screens)
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
        },
            actions =
            {
                if (canShare) {
                    IconButton(onClick = {
                        Picasso.get().load(DogsAppContext.imageNowShowing).into(
                            object : com.squareup.picasso.Target {
                                override fun onBitmapLoaded(
                                    bitmap: Bitmap?,
                                    from: Picasso.LoadedFrom?
                                ) {
                                    val intent = Intent().apply {
                                        this.action = Intent.ACTION_SEND
                                    }
                                    intent.type = "image/png"
                                    intent.putExtra(
                                        Intent.EXTRA_STREAM,
                                        getLocalBitmapUri(bitmap!!, context)
                                    )
                                    context.startActivity(Intent.createChooser(intent, "Share"))
                                }

                                override fun onBitmapFailed(
                                    e: Exception?,
                                    errorDrawable: Drawable?
                                ) {
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                }

                            }
                        )
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share this image")
                    }
                }
            })
    }, bottomBar = {
        if (screenNow in screens)
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            if (screen == "Dogs")
                                Icon(
                                    painter = painterResource(id = R.drawable.pets),
                                    contentDescription = null
                                )
                            else
                                Icon(Icons.Default.Favorite, null)
                        },
                        label = { Text(screen) },
                        selected = currentDestination?.hierarchy?.any {
                            screen in (it.route ?: "")
                        } == true,
                        onClick = { navController.navigate(screen) }
                    )
                }

            }
    }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = screens[0],
            modifier = Modifier.padding(padding)
        ) {
            composable(screens[0]) {
                Dogs(navController)
                screenNow = it.destination.route!!
                canShare = false
            }
            composable(screens[1]) {
                Likes(navController)
                screenNow = it.destination.route!!
                canShare = false
            }
            composable(
                "subBreeds/{subBreeds}",
                arguments = listOf(navArgument("subBreeds") { type = NavType.StringType })
            ) { navBackStackEntry ->
                val obj = JSONObject(navBackStackEntry.arguments?.getString("subBreeds")!!)
                Dogs(
                    navController = navController,
                    obj.getJSONArray("subBreeds"),
                    obj.getString("parent")
                )
                screenNow = obj.getString("parent")
                canShare = false
            }
            composable(
                "view/{data}",
                arguments = listOf(navArgument("data") { type = NavType.StringType })
            ) { navBackStackEntry ->
                val jsonArray = JSONArray(navBackStackEntry.arguments?.getString("data")!!)
                screenNow = jsonArray.getString(0)
                if (jsonArray.length() == 1)
                    ImageViewerByBreed(breed = jsonArray.getString(0))
                else {
                    ImageViewerByBreed(
                        breed = jsonArray.getString(0),
                        subBreed = jsonArray.getString(1)
                    )
                    screenNow += " " + jsonArray.getString(1)
                }
                canShare = true

            }
            composable(
                "likes_view/{data}",
                arguments = listOf(navArgument("data") { type = NavType.StringType })
            ) { navBackStackEntry ->
                screenNow = navBackStackEntry.arguments?.getString("data")!!
                ImageViewer(images = DogsAppContext.likes.filter { dog -> dog.breedName == screenNow }
                    .map { dog -> dog.imageUrl }.toList(), fullBreedName = screenNow)
                canShare = true
            }
        }
    }
}