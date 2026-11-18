package mn.intermed.kiosk

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import mn.intermed.kiosk.ui.diagnostics.DiagnosticsScreen
import mn.intermed.kiosk.ui.diagnostics.DiagnosticsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DiagnosticsViewModel by viewModels()
    private val scanner by lazy { (application as KioskApp).container.scanner }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MaterialTheme {
                DiagnosticsScreen(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDevices()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return scanner.onKeyEvent(event) || super.dispatchKeyEvent(event)
    }
}
