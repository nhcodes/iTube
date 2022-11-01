package dev.nh7.itube.frontend.modifiers

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

class DraggableState {
    var offset by mutableStateOf(IntOffset.Zero)

    fun addOffset(otherOffset: IntOffset) {
        offset = offset.plus(otherOffset)
    }
}

fun Modifier.draggableAfterLongClick(
    state: DraggableState,
    onDrag: ((Offset) -> Unit) = {},
    onDragStart: ((Offset) -> Unit) = {},
    onDragEnd: (() -> Unit) = {},
    onDragCancel: (() -> Unit) = {}
): Modifier = this
    .offset { state.offset }
    .pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, dragAmount ->
                change.consume()
                val offsetChange = IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
                state.addOffset(offsetChange)
                onDrag(dragAmount)
            },
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDragCancel = onDragCancel
        )
    }