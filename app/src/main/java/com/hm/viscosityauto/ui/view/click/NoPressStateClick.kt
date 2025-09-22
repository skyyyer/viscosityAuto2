import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 自定义点击效果（可控制按压状态背景色变化）并添加防重逻辑
 * @param onClick 点击回调
 * @param onLongClick 长按回调（带有位置信息）
 * @param pressedColor 按压状态背景色
 * @param idleColor 默认状态背景色
 * @param shape 背景形状
 * @param minClickIntervalMs 防止重复点击的最小时间间隔（毫秒），默认为 500ms
 */
fun Modifier.NoPressStateClick(
    onClick: () -> Unit = {},
    onLongClick: (Offset) -> Unit = {},
    pressedColor: Color = Color.Transparent,
    idleColor: Color = Color.Transparent,
    shape: Shape = RectangleShape,
    minClickIntervalMs: Long = 500
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState by interactionSource.collectIsPressedAsState()

    // 使用互斥锁确保点击安全
    val clickMutex = remember { Mutex() }
    val lastClickTime = remember { mutableStateOf(0L) }

    // 使用 rememberUpdatedState 确保回调始终是最新的
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)

    this
        .pointerInput(minClickIntervalMs, currentOnClick, currentOnLongClick) {
            detectTapGestures(
                onPress = { offset ->
                    // 手动管理按压状态
                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)

                    try {
                        tryAwaitRelease()

                        // 处理点击（带防重逻辑）
                        if (clickMutex.tryLock()) {
                            try {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime.value > minClickIntervalMs) {
                                    lastClickTime.value = currentTime
                                    currentOnClick()
                                }
                            } finally {
                                clickMutex.unlock()
                            }
                        }
                    } catch (e: CancellationException) {
                        // 处理长按
                        currentOnLongClick(offset)
                    } finally {
                        interactionSource.emit(PressInteraction.Release(press))
                    }
                },
                onLongPress = { offset ->
                    // 长按时立即调用回调
                    currentOnLongClick(offset)
                }
            )
        }
        .background(color = if (pressState) pressedColor else idleColor, shape = shape)
}