package tw.wtb.floatcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.hjq.xtoast.XToast
import com.hjq.xtoast.draggable.SpringDraggable
import tw.wtb.floatcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mFloatIcon: XToast<XToast<*>>
    private lateinit var mCalculatorView: XToast<XToast<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mFloatIcon = XToast<XToast<*>>(application).apply {
            setContentView(R.layout.float_icon)
            setGravity(Gravity.END)
            setYOffset(-500)
            setDraggable(SpringDraggable())
            setImageDrawable(android.R.id.icon, ResourcesCompat.getDrawable(resources, R.drawable.calculator_icon, null))
            setOnClickListener(android.R.id.icon) { _, viewIcon ->
                if(!mCalculatorView.isShow) {
                    mCalculatorView.showAsDropDown(viewIcon, Gravity.BOTTOM)
                }
                else {
                    mCalculatorView.cancel()
                }
            }
            setOnLongClickListener(android.R.id.icon) { toastIcon, _ ->
                ToastUtils.show("已關閉計算機")
                mCalculatorView.cancel()
                toastIcon.cancel()
                true
            }
        }

        mCalculatorView = XToast<XToast<*>>(application).apply {
            setContentView(R.layout.calculator_view)
            setDraggable(SpringDraggable())
            setOnClickListener(R.id.btnClear) { toastCalc, _ ->
                toastCalc.findViewById<EditText>(R.id.result).setText("0")
            }
        }

        binding.btnStart.setOnClickListener {
            XXPermissions.with(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request (object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        mFloatIcon.show()
                    }
                    override fun onDenied(permissions: List<String>, never: Boolean) {
                        if(ToastUtils.isInit())
                            ToastUtils.show("未授予權限")
                    }
                })
        }
    }
}