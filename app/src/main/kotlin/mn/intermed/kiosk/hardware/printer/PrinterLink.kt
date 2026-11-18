package mn.intermed.kiosk.hardware.printer

import android.hardware.usb.UsbDevice

sealed interface PrinterLink {
    data object UsbAuto : PrinterLink
    data class Usb(val device: UsbDevice) : PrinterLink
}
