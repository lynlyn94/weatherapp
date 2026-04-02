package com.example.weatherapp.ui.screens

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.model.CurrentDto
import com.example.weatherapp.data.model.ForecastDayDto
import com.example.weatherapp.data.model.HourDto
import com.example.weatherapp.ui.viewmodel.WeatherViewModel
import com.example.weatherapp.util.IconMapper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedCities by viewModel.savedCities.collectAsState()

    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadSavedCities(context)
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(key1 = locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            viewModel.refreshWeather(context)
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(20.dp))
                Text(
                    "城市管理",
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.LocationOn, null) },
                    label = { Text("📍 当前定位") },
                    selected = false,
                    onClick = {
                        viewModel.refreshWeather(context)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.height(10.dp))
                Text("已保存城市", modifier = Modifier.padding(start = 20.dp), style = MaterialTheme.typography.labelLarge)

                if (savedCities.isEmpty()) {
                    Text("暂无保存记录", modifier = Modifier.padding(20.dp), color = Color.Gray)
                } else {
                    savedCities.forEach { city ->
                        NavigationDrawerItem(
                            label = { Text(city) },
                            selected = false,
                            onClick = {
                                viewModel.searchCity(context, city)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (isSearchActive) {
                    TopAppBar(
                        title = {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("输入城市 (如: Beijing)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            }) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        },
                        actions = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    viewModel.searchCity(context, searchQuery)
                                    isSearchActive = false
                                    searchQuery = ""
                                }) {
                                    Icon(Icons.Default.Search, "Search")
                                }
                            }
                        }
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = uiState.locationName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.error != null) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            if (locationPermissionState.status.isGranted) {
                                viewModel.refreshWeather(context)
                            }
                        }) {
                            Text("重试")
                        }
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp)
                    ) {
                        uiState.weatherNow?.let { now ->
                            MainWeatherCard(now)
                            Spacer(modifier = Modifier.height(16.dp))

                            val hourlyData = uiState.weatherHourly
                            if (hourlyData.isNotEmpty()) {
                                Text("24小时预报", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                HourlyForecastRow(hourlyData)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("生活指数", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            WeatherDetailsGrid(now)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("未来 5 天预报", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.weatherDaily.forEach { daily ->
                            ForecastItem(daily)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MainWeatherCard(weather: CurrentDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = IconMapper.getWeatherIconResId(weather.icon)),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
            Text(text = "${weather.temp}°", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
            Text(text = weather.text, style = MaterialTheme.typography.titleLarge)
            Text(text = "体感 ${weather.feelsLike}°  |  ${weather.windDir}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun HourlyForecastRow(hours: List<HourDto>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(hours) { hour ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = hour.timeOnly, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        painter = painterResource(id = IconMapper.getWeatherIconResId(hour.icon)),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${hour.temp}°", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WeatherDetailsGrid(weather: CurrentDto) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Air,
            title = "空气质量",
            value = weather.aqiLevel,
            subValue = "指数: ${weather.aqi?.usEpaIndex ?: "N/A"}"
        )
        DetailCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.WaterDrop,
            title = "湿度",
            value = "${weather.humidity}%",
            subValue = "UV指数: ${weather.uvIndex}"
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.WbSunny,
            title = "紫外线",
            value = weather.uvIndex,
            subValue = if (weather.uv.toInt() > 5) "注意防晒" else "适宜外出"
        )
    }
}

@Composable
fun DetailCard(modifier: Modifier = Modifier, icon: ImageVector, title: String, value: String, subValue: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subValue, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// --- 最终修复版 ForecastItem ---
@Composable
fun ForecastItem(daily: ForecastDayDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            // 关键点：保持垂直居中，这样即使文字换行，图标和日期依然居中对齐
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 日期列 (权重 1.2f) - 靠左固定
            Text(
                text = daily.date.takeLast(5),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1.2f)
            )

            // 2. 图标列 (权重 0.5f) - 作为一个独立的格子居中
            Box(
                modifier = Modifier.weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = IconMapper.getWeatherIconResId(daily.iconDay)),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            // 3. 描述文字列 (权重 1.3f) - 紧跟图标格子，靠左对齐
            Text(
                text = daily.textDay,
                style = MaterialTheme.typography.bodyMedium,
                // 【修改点在这里】
                // 原来是 maxLines = 1，导致被截断。
                // 现在改为 maxLines = 2，允许它显示两行。
                maxLines = 2,
                // 如果极端情况下超过2行，再用省略号(虽然不太可能发生)
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1.3f)
            )

            // 4. 温度列 (权重 1.0f) - 靠右固定
            Text(
                text = "${daily.tempMin}° / ${daily.tempMax}°",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}