package com.plcoding.jetpackcomposepokedex.ui.screens.pokemondetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.plcoding.jetpackcomposepokedex.R
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Type
import com.plcoding.jetpackcomposepokedex.util.*

@Composable
@Preview
fun PokemonDetailTopSectionPreview() {
    PokemonDetailTopSection(navController = rememberNavController())
}

@Composable
fun PokemonDetailScreen(
    id: Int,
    navController: NavController,
) {
    val viewModel = hiltViewModel<PokemonDetailViewModel>()
    viewModel.getPokemonInfo(id)
    PokemonDetailsScreenInitialLayout(viewModel, id, navController)
}

@Composable
private fun PokemonDetailsScreenInitialLayout(
    viewModel : PokemonDetailViewModel,
    id: Int,
    navController: NavController
) {
    val pokemonInfo = viewModel.pokemonInfo.observeAsState().value

    var dominantColor by remember {
        mutableStateOf(Color.White)
    }

    val pokemonImageUrl = viewModel.getPokemonImageUrl(id)

    val image = loadImage(url = pokemonImageUrl).value
    if (image != null) {
        calcDominantColor(image){
            dominantColor = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 16.dp)
    ){
        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .align(Alignment.TopCenter)
        )
        if (pokemonInfo != null) {
            PokemonDetailStateWrapper(
                pokemonInfo = pokemonInfo,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 120.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colors.surface)
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                loadingModifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
                    .padding(
                        top = 120.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            )
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            if (image != null) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = pokemonInfo?.data?.name,
                    modifier = Modifier
                        .size(200.dp)
                        .offset(y = 20.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(x = 16.dp, y = 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when(pokemonInfo){
        is Resource.Success -> {
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data ?: return,
                modifier = modifier
                    .offset(y = (-20).dp)
            )
        }
        is Resource.Error -> {
            Text(
                text = pokemonInfo.message ?: "",
                color = Color.Red,
                modifier = modifier,
                textAlign = TextAlign.Center
            )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "#${pokemonInfo.id} ${pokemonInfo.name.uppercase()}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            color = MaterialTheme.colors.onSurface
        )
        PokemonTypeSection(types = pokemonInfo.types)
        PokemonDetailDataSection(
            pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }
}

@Composable
fun PokemonTypeSection(types: List<Type>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
    ) {
        for (type in types){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .shadow(5.dp, CircleShape)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .shadow(10.dp, CircleShape)
                    .height(35.dp)
            ) {
                Text(
                    text = type.type.name.replaceFirstChar { it.uppercaseChar() },
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int
) {
    val pokemonWeightInKg = remember {
        pokemonWeight / 10f
    }
    val pokemonHeightInMeters = remember {
        pokemonHeight / 10f
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            dataIcon = painterResource(id = R.drawable.ic_weight),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(Color.LightGray)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_height),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
    ) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue $dataUnit",
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed){
            statValue / statMaxValue.toFloat()
        }else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )
    LaunchedEffect(key1 = true){
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color.DarkGray
                } else Color.LightGray
            )
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(
                    brush = Brush.horizontalGradient(
                        if (isSystemInDarkTheme()) {
                            listOf(
                                Color.Black,
                                statColor
                            )
                        } else {
                            listOf(
                                Color.White,
                                statColor
                            )
                        }
                    )
                )
                .padding(horizontal = 8.dp)
        ){
            Text(
                text = statName,
                fontWeight = FontWeight.Bold,
                color = if (isSystemInDarkTheme()){
                    statColor
                }else{
                    Color.Black
                }
            )
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100
) {
    val statMaxValue = remember {
        100
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Base Stats:",
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        for ((i, stat) in pokemonInfo.stats.withIndex()){
            PokemonStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.baseStat,
                statMaxValue = statMaxValue,
                statColor = parseStatToColor(stat),
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}