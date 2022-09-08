package su.pank.ilovedogs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.json.JSONObject
import java.util.*


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    val screens = listOf("Dogs", "Likes")
    var screenNow by remember {
        mutableStateOf(screens[0])
    }
    Scaffold(topBar = {
        SmallTopAppBar(title = {
            Text(
                text = screenNow.capitalize(Locale.ROOT)
            )
        }, navigationIcon = {
            if (screenNow !in screens)
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
        })
    }, bottomBar = {
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
            }
            composable(screens[1]) {
                Text(screens[1])
                screenNow = it.destination.route!!
            }
            composable(
                "subBreeds/{subBreeds}",
                arguments = listOf(navArgument("subBreeds") { type = NavType.StringType })
            ) {

                    navBackStackEntry ->
                Dogs(
                    navController = navController,
                    JSONObject(navBackStackEntry.arguments?.getString("subBreeds")!!).getJSONArray("subBreeds"),
                    JSONObject(navBackStackEntry.arguments?.getString("subBreeds")!!).getString("parent")
                )
                screenNow = JSONObject(navBackStackEntry.arguments?.getString("subBreeds")!!).getString("parent")

            }
        }
    }
}