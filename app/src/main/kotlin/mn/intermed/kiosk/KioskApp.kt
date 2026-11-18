package mn.intermed.kiosk

import android.app.Application
import android.content.pm.ApplicationInfo
import com.szsicod.print.log.AndroidLogCatStrategy
import com.szsicod.print.log.Logger
import com.szsicod.print.log.Utils

class KioskApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Logger.addLogStrategy(AndroidLogCatStrategy())
        }
        container = AppContainer(this)
    }
}
