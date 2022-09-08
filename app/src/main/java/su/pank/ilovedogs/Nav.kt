package su.pank.ilovedogs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(navController: NavHostController = rememberNavController()){
    val screens = listOf("Dogs", "Likes")

    Scaffold(bottomBar = { NavigationBar(){
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach{ screen ->
            NavigationBarItem(
                icon = { if (screen == "Dogs")
                    Icon(painter = painterResource(id = R.drawable.pets), contentDescription = null)
                       else 
                       Icon(Icons.Default.Favorite, null)},
                label = { Text(screen) },
                selected = currentDestination?.hierarchy?.any{it.route == screen} == true,
                onClick = { navController.navigate(screen) }
            )
        }

        }
    }
    ){padding -> NavHost(navController = navController, startDestination = screens[0], modifier = Modifier.padding(padding)){
        composable(screens[0]){ Dogs() }
        composable(screens[1]) { Text(screens[1]) }
    }}
}