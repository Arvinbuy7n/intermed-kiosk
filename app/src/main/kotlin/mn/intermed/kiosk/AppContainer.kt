package mn.intermed.kiosk

import android.content.Context
import mn.intermed.kiosk.hardware.printer.IcodPrinter
import mn.intermed.kiosk.hardware.scanner.KeyboardScanner
import mn.intermed.kiosk.hardware.usb.UsbDevices

class AppContainer(context: Context) {
    val printer = IcodPrinter(context)
    val scanner = KeyboardScanner()
    val usbDevices = UsbDevices(context)
}
