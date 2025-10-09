package pe.edu.upc.engitrack.features.home.presentation.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pe.edu.upc.engitrack.R
import pe.edu.upc.engitrack.core.ui.components.RoundedIcon
import pe.edu.upc.engitrack.core.ui.components.WidthSpacer

@Composable
fun Home(viewModel: HomeViewModel = hiltViewModel(), onTapProductCard: (Int) -> Unit) {

    val categories = listOf("All", "Men", "Women", "Boys", "Girls")

    val selectedCategory = remember {
        mutableStateOf("All")
    }

    val products by viewModel.products.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            WidthSpacer()
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Hello Alex",
                )
                Text(
                    "Good morning!",
                    fontWeight = FontWeight.SemiBold
                )
            }
            WidthSpacer()
            RoundedIcon(Icons.Outlined.Notifications)
            WidthSpacer()
            RoundedIcon(Icons.Outlined.ShoppingCart)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)

        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                placeholder = { Text(stringResource(R.string.placeholder_search)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            )
            WidthSpacer()
            RoundedIcon(Icons.Outlined.FilterList)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                stringResource(R.string.label_categories),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = {}) { Text(stringResource(R.string.button_see_all)) }
        }

        LazyRow {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory.value == category,
                    onClick = {
                        selectedCategory.value = category
                    },
                    label = { Text(category) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Box(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Get your special sale up to 40%",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    ElevatedButton(onClick = {}) { Text("Shop now") }
                }
                Image(
                    painterResource(R.drawable.banner),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),

                    )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                stringResource(R.string.label_products),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = {}) { Text(stringResource(R.string.button_see_all)) }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(products) { product ->
                ProductCard(product){
                    Log.d("Home", product.id.toString())
                    onTapProductCard(product.id)
                }
            }
        }
    }
}
