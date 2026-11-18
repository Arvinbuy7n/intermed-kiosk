package mn.intermed.kiosk.hardware.printer

sealed interface ReceiptLine {

    data class Text(
        val text: String,
        val sizePx: Float = ReceiptRenderer.BODY_SIZE_PX,
        val bold: Boolean = false,
        val align: Align = Align.START,
    ) : ReceiptLine

    data class Row(
        val left: String,
        val right: String,
        val bold: Boolean = false,
    ) : ReceiptLine

    data class Qr(val content: String, val sizePx: Int = 280) : ReceiptLine

    data class Gap(val heightPx: Int = 16) : ReceiptLine

    data object Divider : ReceiptLine

    enum class Align { START, CENTER, END }
}
