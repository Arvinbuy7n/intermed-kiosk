package mn.intermed.kiosk.hardware.printer

import android.content.Context
import android.graphics.Bitmap
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class IcodPrinter(private val context: Context) {

    private val api: PrinterAPI by lazy { PrinterAPI.getInstance() }
    private val mutex = Mutex()

    suspend fun connect(link: PrinterLink): Int = exclusive {
        if (api.isConnect()) api.disconnect()
        api.connect(open(link))
    }

    suspend fun disconnect(): Int = exclusive { api.disconnect() }

    suspend fun status(): PrinterStatus = exclusive { PrinterStatus(api.getStatus()) }

    suspend fun printBitmap(bitmap: Bitmap): Int = exclusive {
        val code = api.printRasterBitmap(bitmap)
        feedAndCut()
        code
    }

    suspend fun printText(text: String, charset: String, codeTable: Int): Int = exclusive {
        api.setCharCodeTable(codeTable)
        val code = api.printString(text, charset, true)
        feedAndCut()
        code
    }

    suspend fun isConnected(): Boolean = try {
        exclusive { api.isConnect() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    } catch (e: LinkageError) {
        false
    }

    private fun open(link: PrinterLink): InterfaceAPI = when (link) {
        PrinterLink.UsbAuto -> USBAPI(context)
        is PrinterLink.Usb -> USBAPI(context, link.device)
    }

    private fun feedAndCut() {
        api.printFeed()
        api.cutPaper(CUT_FEED_AND_PARTIAL, 0)
    }

    private suspend fun <T> exclusive(block: () -> T): T =
        mutex.withLock { withContext(Dispatchers.IO) { block() } }

    companion object {
        const val SUCCESS = 0
        const val CODE_TABLE_PC866 = 17
        private const val CUT_FEED_AND_PARTIAL = 0x42
    }
}
