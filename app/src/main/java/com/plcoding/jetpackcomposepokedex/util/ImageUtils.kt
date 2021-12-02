package com.plcoding.jetpackcomposepokedex.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun loadImage(url: String): MutableState<Bitmap?> {

    val bitmapState: MutableState<Bitmap?> = remember {
        mutableStateOf(null)
    }

    Glide.with(LocalContext.current)
        .asBitmap()
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(object: CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }
            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    return bitmapState
}

fun calcDominantColor(bmp: Bitmap, onFinish: (Color) -> Unit){
    Palette.from(bmp).generate{ palette ->
        palette?.dominantSwatch?.rgb?.let { colorValue ->
            onFinish(Color(colorValue))
        }
    }
}