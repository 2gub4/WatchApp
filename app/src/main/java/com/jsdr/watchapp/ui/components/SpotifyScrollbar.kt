package com.jsdr.watchapp.ui.components
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.jsdr.watchapp.BrandPurple
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun SpotifyScrollbar(
    gridState: LazyGridState,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    // Animacja grubości paska (4.dp w spoczynku, 8.dp podczas ciągnięcia)
    val thumbWidth by animateDpAsState(targetValue = if (isDragging) 8.dp else 4.dp, label = "scrollbar_width")
    val coroutineScope = rememberCoroutineScope()

    // Odczyt układu siatki w celu wyliczenia proporcji scrolla
    val layoutInfo = gridState.layoutInfo
    val totalItemsCount = layoutInfo.totalItemsCount
    if (totalItemsCount == 0) return

    val firstVisibleItemIndex = gridState.firstVisibleItemIndex
    val visibleItemsCount = layoutInfo.visibleItemsInfo.size

    // Estymacja procentowa pozycji (od 0.0 do 1.0)
    val scrollProportion = if (totalItemsCount > visibleItemsCount) {
        firstVisibleItemIndex.toFloat() / (totalItemsCount - visibleItemsCount).toFloat()
    } else 0f

    // Wielkość kciuka przewijania w zależności od ilości treści
    val thumbSizeProportion = (visibleItemsCount.toFloat() / totalItemsCount.toFloat()).coerceIn(0.1f, 1f)

    BoxWithConstraints(
        modifier = modifier
            // Poszerzyliśmy strefę dotyku do 32.dp, żeby łatwiej było "złapać" pasek palcem
            .width(32.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false }
                ) { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        // Dynamiczny mnożnik: im więcej elementów, tym szybciej musimy scrollować,
                        // aby nadążyć za ruchem paska na ekranie.
                        val scrollMultiplier = if (visibleItemsCount > 0) {
                            (totalItemsCount.toFloat() / visibleItemsCount) * 1.5f
                        } else {
                            5f
                        }
                        // Używamy scrollBy - dodatni dragAmount (w dół) przesunie listę w dół.
                        gridState.scrollBy(dragAmount * scrollMultiplier)
                    }
                }
            }
    ) {
        val trackHeight = maxHeight.value
        val thumbHeight = max(trackHeight * thumbSizeProportion, 30f).dp // Minimum 30px wysokości

        // Zabezpieczenie przed wyjechaniem paska poza dolną krawędź ekranu
        val maxOffsetY = maxHeight - thumbHeight
        val offsetY = (maxOffsetY * scrollProportion).coerceIn(0.dp, maxOffsetY)

        // Widoczny pasek
        Box(
            modifier = Modifier
                .offset(y = offsetY)
                .width(thumbWidth)
                .height(thumbHeight)
                .clip(RoundedCornerShape(50))
                .background(if (isDragging) Color.White else BrandPurple)
                // Wyrównanie do prawej krawędzi, aby strefa dotyku (32.dp) "wystawała" w lewo, a sam pasek przylegał do boku
                .align(Alignment.TopEnd)
        )
    }
}
/// wersja do każdej karty(bo chuj wie nie działa i chuj)
@Composable
fun SpotifyScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    var isDragging by remember {
        mutableStateOf(false)
    }

    val thumbWidth by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 4.dp,
        label = "scrollbar_width"
    )

    val coroutineScope = rememberCoroutineScope()

    val layoutInfo = listState.layoutInfo
    val totalItemsCount = layoutInfo.totalItemsCount

    if (totalItemsCount == 0) return

    val firstVisibleItemIndex =
        listState.firstVisibleItemIndex

    val visibleItemsCount =
        layoutInfo.visibleItemsInfo.size

    val scrollProportion =
        if (totalItemsCount > visibleItemsCount) {

            firstVisibleItemIndex.toFloat() /
                    (totalItemsCount - visibleItemsCount).toFloat()

        } else {
            0f
        }

    val thumbSizeProportion =
        (visibleItemsCount.toFloat() / totalItemsCount.toFloat())
            .coerceIn(0.1f, 1f)

    BoxWithConstraints(

        modifier = modifier
            .width(32.dp)

    ) {

        val trackHeight = maxHeight.value

        val thumbHeight =
            max(trackHeight * thumbSizeProportion, 30f).dp

        val maxOffsetY = maxHeight - thumbHeight

        val offsetY =
            (maxOffsetY * scrollProportion)
                .coerceIn(0.dp, maxOffsetY)

        Box(
            modifier = Modifier
                .offset(y = offsetY)
                .width(thumbWidth)
                .height(thumbHeight)
                .clip(RoundedCornerShape(50))
                .background(
                    if (isDragging)
                        Color.White
                    else
                        BrandPurple
                )
                .align(Alignment.TopEnd)
        )
    }
}