package mn.intermed.kiosk.hardware.printer

data class PrinterStatus(val raw: Int) {

    val reachable: Boolean
        get() = raw >= 0

    val flags: List<PrinterFlag>
        get() = if (reachable) PrinterFlag.entries.filter { (raw and it.mask) != 0 } else emptyList()

    val canPrint: Boolean
        get() = reachable && flags.none { it.blocksPrinting }

    fun describe(): String = when {
        !reachable -> "Принтерээс хариу ирсэнгүй"
        flags.isEmpty() -> "Хэвийн"
        else -> flags.joinToString { it.label }
    }
}
