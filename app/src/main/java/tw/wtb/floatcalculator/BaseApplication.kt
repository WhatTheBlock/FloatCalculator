package tw.wtb.floatcalculator

import android.app.Application
import com.hjq.toast.ToastUtils

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 初始化Toast框架
        ToastUtils.init(this)
    }
}