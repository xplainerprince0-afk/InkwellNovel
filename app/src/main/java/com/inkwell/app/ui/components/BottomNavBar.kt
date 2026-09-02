package com.inkwell.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Globe
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Globe
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.inkwell.app.R

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelResId: Int,
    val contentDescriptionResId: Int
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = "home",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book,
        labelResId = R.string.nav_home,
        contentDescriptionResId = R.string.nav_home_desc
    ),
    BottomNavItem(
        route = "write",
        selectedIcon = Icons.Filled.Edit,
        unselectedIcon = Icons.Outlined.Edit,
        labelResId = R.string.nav_write,
        contentDescriptionResId = R.string.nav_write_desc
    ),
    BottomNavItem(
        route = "world",
        selectedIcon = Icons.Filled.Globe,
        unselectedIcon = Icons.Outlined.Globe,
        labelResId = R.string.nav_world,
        contentDescriptionResId = R.string.nav_world_desc
    ),
    BottomNavItem(
        route = "settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        labelResId = R.string.nav_settings,
        contentDescriptionResId = R.string.nav_settings_desc
    )
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            val contentDescription = stringResource(item.contentDescriptionResId)

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(item.labelResId)) },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                modifier = Modifier.semantics {
                    this.contentDescription = contentDescription
                }
            )
        }
    }
}
