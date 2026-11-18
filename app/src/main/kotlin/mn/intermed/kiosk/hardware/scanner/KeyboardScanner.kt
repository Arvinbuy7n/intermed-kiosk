package mn.intermed.kiosk.hardware.scanner

import android.view.KeyCharacterMap
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class KeyboardScanner {

    private val buffer = StringBuilder()
    private var lastKeyAt = 0L

    private val _scans = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val scans: SharedFlow<String> = _scans.asSharedFlow()

    fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.deviceId == KeyCharacterMap.VIRTUAL_KEYBOARD) return false
        val isEnter = event.keyCode == KeyEvent.KEYCODE_ENTER ||
            event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
        val codePoint = event.unicodeChar
        if (!isEnter && codePoint <= 0) return false
        if (event.action != KeyEvent.ACTION_DOWN) return true

        if (event.eventTime - lastKeyAt > MAX_KEY_GAP_MS) buffer.setLength(0)
        lastKeyAt = event.eventTime

        if (isEnter) {
            emitScan()
        } else if (!Character.isISOControl(codePoint)) {
            buffer.appendCodePoint(codePoint)
        }
        return true
    }

    private fun emitScan() {
        val code = buffer.toString().trim()
        buffer.setLength(0)
        if (code.isNotEmpty()) _scans.tryEmit(code)
    }

    private companion object {
        const val MAX_KEY_GAP_MS = 500L
    }
}
