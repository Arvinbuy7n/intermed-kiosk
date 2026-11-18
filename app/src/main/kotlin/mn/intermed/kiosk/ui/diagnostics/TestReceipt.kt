package mn.intermed.kiosk.ui.diagnostics

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import mn.intermed.kiosk.hardware.printer.ReceiptLine

object TestReceipt {

    const val CYRILLIC_TEXT = "Intermed Kiosk 0123456789\n" +
        "АБВГДЕЁЖЗИЙКЛМНОӨПРСТУҮФХЦЧШЩЪЫЬЭЮЯ\n" +
        "абвгдеёжзийклмноөпрстуүфхцчшщъыьэюя\n"

    fun lines(now: Date = Date()): List<ReceiptLine> = listOf(
        ReceiptLine.Text("ИНТЕРМЕД ЭМНЭЛЭГ", sizePx = 34f, bold = true, align = ReceiptLine.Align.CENTER),
        ReceiptLine.Text("ТУРШИЛТЫН БАРИМТ", sizePx = 26f, align = ReceiptLine.Align.CENTER),
        ReceiptLine.Gap(),
        ReceiptLine.Divider,
        ReceiptLine.Row("Огноо", formatDate(now)),
        ReceiptLine.Row("Киоск", "K8-TEST"),
        ReceiptLine.Divider,
        ReceiptLine.Row("Үзлэгийн төлбөр", "45,000₮"),
        ReceiptLine.Row("Шинжилгээ (өлөн үеийн цусан дахь сахар)", "12,500₮"),
        ReceiptLine.Row("Хөнгөлөлт", "-2,500₮"),
        ReceiptLine.Divider,
        ReceiptLine.Row("НИЙТ ДҮН", "55,000₮", bold = true),
        ReceiptLine.Row("Төлбөрийн хэлбэр", "QPay"),
        ReceiptLine.Gap(),
        ReceiptLine.Text("Өө Үү Ёё Ъъ Ыы Ьь Ээ Юю Яя", align = ReceiptLine.Align.CENTER),
        ReceiptLine.Gap(),
        ReceiptLine.Qr("INTERMED-KIOSK-PRINT-TEST"),
        ReceiptLine.Gap(),
        ReceiptLine.Text(
            "Энэ бол туршилтын хэвлэлт. Төлбөрийн баримт биш.",
            sizePx = 20f,
            align = ReceiptLine.Align.CENTER,
        ),
        ReceiptLine.Gap(24),
    )

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        format.timeZone = TimeZone.getTimeZone("Asia/Ulaanbaatar")
        return format.format(date)
    }
}
