package mn.intermed.kiosk.hardware.printer

enum class PrinterFlag(val mask: Int, val blocksPrinting: Boolean, val label: String) {
    OFFLINE(0x01, true, "Офлайн"),
    FEEDING(0x02 or 0x10, false, "Цаас татаж байна"),
    ERROR(0x04, true, "Алдаа гарсан"),
    OUT_OF_PAPER(0x08 or 0x400, true, "Цаас дууссан"),
    COVER_OPEN(0x20, true, "Таг онгорхой"),
    RECOVERABLE_ERROR(0x40, true, "Өөрөө сэргэх алдаа"),
    UNRECOVERABLE_ERROR(0x80, true, "Сэргэхгүй алдаа"),
    MECHANICAL_ERROR(0x100, true, "Механик алдаа"),
    PAPER_NEAR_END(0x200, false, "Цаас дуусах дөхсөн"),
}
