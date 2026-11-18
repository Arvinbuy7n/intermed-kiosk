package mn.intermed.kiosk.ui.diagnostics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mn.intermed.kiosk.KioskApp
import mn.intermed.kiosk.hardware.printer.IcodPrinter
import mn.intermed.kiosk.hardware.printer.PrinterLink
import mn.intermed.kiosk.hardware.printer.ReceiptRenderer
import mn.intermed.kiosk.hardware.usb.UsbDeviceInfo

class DiagnosticsViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as KioskApp).container
    private val printer = container.printer
    private val usbDevices = container.usbDevices

    private val _state = MutableStateFlow(DiagnosticsState())
    val state: StateFlow<DiagnosticsState> = _state.asStateFlow()

    init {
        refreshDevices()
        connectUsbAuto()
        viewModelScope.launch {
            container.scanner.scans.collect { code ->
                _state.update { it.copy(lastScan = code, scanCount = it.scanCount + 1) }
            }
        }
    }

    fun refreshDevices() {
        _state.update { it.copy(usbDevices = usbDevices.list()) }
    }

    fun connectUsbAuto() {
        connect("USB (автомат)", PrinterLink.UsbAuto)
    }

    fun connectUsb(info: UsbDeviceInfo) {
        if (!info.hasPermission) {
            usbDevices.requestPermission(info.device)
            _state.update {
                it.copy(printerMessage = "USB зөвшөөрөл хүслээ. Зөвшөөрсний дараа дахин холбоно уу.")
            }
            return
        }
        connect(info.name, PrinterLink.Usb(info.device))
    }

    fun disconnect() {
        runPrinter("Салгах") {
            "Салгалаа (код ${printer.disconnect()})"
        }
    }

    fun readStatus() {
        runPrinter("Төлөв унших") {
            val status = printer.status()
            "Төлөв: ${status.describe()} (код ${status.raw})"
        }
    }

    fun printTestReceipt() {
        runPrinter("Туршилтын баримт") {
            val bitmap = withContext(Dispatchers.Default) {
                ReceiptRenderer.render(TestReceipt.lines())
            }
            printResult("Туршилтын баримт", printer.printBitmap(bitmap))
        }
    }

    fun printCyrillicText() {
        runPrinter("Кирилл текст") {
            val code = printer.printText(
                text = TestReceipt.CYRILLIC_TEXT,
                charset = "CP866",
                codeTable = IcodPrinter.CODE_TABLE_PC866,
            )
            printResult("Кирилл текст", code)
        }
    }

    private fun connect(label: String, link: PrinterLink) {
        runPrinter("Холбож байна: $label") {
            val code = printer.connect(link)
            if (code == IcodPrinter.SUCCESS) "Холбогдлоо: $label" else "Холбогдсонгүй: $label (код $code)"
        }
    }

    private fun printResult(label: String, code: Int): String =
        if (code == IcodPrinter.SUCCESS) "$label хэвлэгдлээ" else "$label хэвлэгдсэнгүй (код $code)"

    private fun runPrinter(label: String, action: suspend () -> String) {
        if (_state.value.printerBusy) return
        _state.update { it.copy(printerBusy = true, printerMessage = label) }
        viewModelScope.launch {
            val message = try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failureMessage(label, e)
            } catch (e: LinkageError) {
                failureMessage(label, e)
            }
            val connected = printer.isConnected()
            _state.update {
                it.copy(printerBusy = false, printerMessage = message, printerConnected = connected)
            }
        }
    }

    private fun failureMessage(label: String, error: Throwable): String =
        "$label: алдаа гарлаа (${error.message ?: error.javaClass.simpleName})"
}
