package mn.intermed.kiosk.hardware.printer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import kotlin.math.ceil
import mn.intermed.kiosk.util.QrBitmap

object ReceiptRenderer {

    const val WIDTH_PX = 576
    const val BODY_SIZE_PX = 24f

    private const val ROW_GAP_PX = 16
    private const val DIVIDER_HEIGHT_PX = 20
    private const val LINE_SPACING_PX = 4f

    private class Block(val height: Int, val draw: (Canvas) -> Unit)

    fun render(lines: List<ReceiptLine>): Bitmap {
        val blocks = lines.map(::toBlock)
        val height = blocks.sumOf { it.height }.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(WIDTH_PX, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        var top = 0
        for (block in blocks) {
            canvas.save()
            canvas.translate(0f, top.toFloat())
            block.draw(canvas)
            canvas.restore()
            top += block.height
        }
        return bitmap
    }

    private fun toBlock(line: ReceiptLine): Block = when (line) {
        is ReceiptLine.Text -> textBlock(line)
        is ReceiptLine.Row -> rowBlock(line)
        is ReceiptLine.Qr -> qrBlock(line)
        is ReceiptLine.Gap -> Block(line.heightPx) { }
        ReceiptLine.Divider -> dividerBlock()
    }

    private fun textBlock(line: ReceiptLine.Text): Block {
        val layout = layout(line.text, textPaint(line.sizePx, line.bold), WIDTH_PX, alignment(line.align))
        return Block(layout.height) { canvas -> layout.draw(canvas) }
    }

    private fun rowBlock(line: ReceiptLine.Row): Block {
        val paint = textPaint(BODY_SIZE_PX, line.bold)
        val rightWidth = ceil(paint.measureText(line.right)).toInt().coerceIn(1, WIDTH_PX / 2)
        val leftWidth = WIDTH_PX - rightWidth - ROW_GAP_PX
        val left = layout(line.left, paint, leftWidth, Layout.Alignment.ALIGN_NORMAL)
        val right = layout(line.right, paint, rightWidth, Layout.Alignment.ALIGN_OPPOSITE)
        return Block(maxOf(left.height, right.height)) { canvas ->
            left.draw(canvas)
            canvas.save()
            canvas.translate((WIDTH_PX - rightWidth).toFloat(), 0f)
            right.draw(canvas)
            canvas.restore()
        }
    }

    private fun qrBlock(line: ReceiptLine.Qr): Block {
        val qr = QrBitmap.create(line.content, line.sizePx)
        return Block(qr.height) { canvas ->
            canvas.drawBitmap(qr, (WIDTH_PX - qr.width) / 2f, 0f, null)
        }
    }

    private fun dividerBlock(): Block {
        val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
            pathEffect = DashPathEffect(floatArrayOf(8f, 6f), 0f)
        }
        val y = DIVIDER_HEIGHT_PX / 2f
        return Block(DIVIDER_HEIGHT_PX) { canvas ->
            canvas.drawLine(0f, y, WIDTH_PX.toFloat(), y, paint)
        }
    }

    private fun textPaint(sizePx: Float, bold: Boolean): TextPaint = TextPaint().apply {
        color = Color.BLACK
        textSize = sizePx
        typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        isAntiAlias = false
    }

    private fun layout(text: String, paint: TextPaint, width: Int, alignment: Layout.Alignment): StaticLayout =
        StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(alignment)
            .setLineSpacing(LINE_SPACING_PX, 1f)
            .setIncludePad(false)
            .build()

    private fun alignment(align: ReceiptLine.Align): Layout.Alignment = when (align) {
        ReceiptLine.Align.START -> Layout.Alignment.ALIGN_NORMAL
        ReceiptLine.Align.CENTER -> Layout.Alignment.ALIGN_CENTER
        ReceiptLine.Align.END -> Layout.Alignment.ALIGN_OPPOSITE
    }
}
