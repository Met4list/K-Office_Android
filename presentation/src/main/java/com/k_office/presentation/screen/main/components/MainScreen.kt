package com.k_office.presentation.screen.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.findActivity
import com.k_office.presentation.screen.shop_list.ShopListFragment
import com.k_office.presentation.screen.home.HomeViewModel

@Composable
internal fun MainScreen(viewModel: HomeViewModel) {

    val context = LocalContext.current

    val currentUser = viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        HeaderGreeting(
            name = currentUser.value?.name.orEmpty(),
            balance = "${currentUser.value?.sum} грн"
        )
        Spacer(modifier = Modifier.height(16.dp))
        StoreLocation(address = "вул. Замостянська, 26", city = "Вінниця") {
            FragmentUtil.setFragmentIfAbsent(ShopListFragment(), context.findActivity(), R.id.nav_container)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
internal fun HeaderGreeting(name: String, balance: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.greetings_title),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = name,
                color = colorResource(R.color.blue_primary),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = stringResource(R.string.balance), color = Color.Gray)
            Text(
                text = balance,
                color = colorResource(R.color.blue_primary),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
internal fun StoreLocation(address: String, city: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.Gray
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = "KOffice $address", fontWeight = FontWeight.Bold)
            Text(text = city, color = Color.Gray)
        }
    }
}

@Composable
internal fun SocialProjects(activeCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "",
            tint = colorResource(R.color.blue_primary),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = "Соціальні проекти", fontWeight = FontWeight.Bold)
            Text(text = "Активні збори: $activeCount", color = Color.Gray)
        }
    }
}

@Composable
internal fun NewsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = stringResource(R.string.news), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        NewsCard(
            title1 = "Масло ПМКК селянське",
            subtitle1 = "солодковершкове 73%, 180 г",
            title2 = "Масло ПМКК Екстра",
            subtitle2 = "солодковершкове 82.5%, 180 г"
        )
    }
}

@Composable
internal fun NewsCard(title1: String, subtitle1: String, title2: String, subtitle2: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title1, fontWeight = FontWeight.Bold)
                Text(text = subtitle1, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = title2, fontWeight = FontWeight.Bold)
                Text(text = subtitle2, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_home),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
