package tw.wtb.floatcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.hjq.xtoast.XToast
import com.hjq.xtoast.draggable.SpringDraggable
import tw.wtb.floatcalculator.databinding.ActivityMainBinding
import tw.wtb.floatcalculator.databinding.CalculatorViewBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var calculatorViewBinding: CalculatorViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        calculatorViewBinding = CalculatorViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val mCalculatorView = XToast<XToast<*>>(application)

        binding.btnStart.setOnClickListener {
            XXPermissions.with(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request (object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        XToast<XToast<*>>(application).apply {
                            setContentView(R.layout.calculator_icon)
                            setGravity(Gravity.END)
                            setYOffset(-500)
                            setDraggable(SpringDraggable())
                            setImageDrawable(android.R.id.icon, ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null))
                            setOnClickListener(android.R.id.icon) { _, view ->
                                mCalculatorView.setContentView(R.layout.calculator_view)
                                mCalculatorView.setDraggable(SpringDraggable())
                                mCalculatorView.setOnClickListener(R.id.btnClose) { toastCalc, _ ->
                                    toastCalc.cancel()
                                }
                                mCalculatorView.showAsDropDown(view, Gravity.BOTTOM)
                            }
                            setOnLongClickListener(android.R.id.icon) { toast, _ ->
                                ToastUtils.show("已關閉計算機")
                                toast.cancel()
                                true
                            }
                        }.show()
                    }
                    override fun onDenied(permissions: List<String>, never: Boolean) {
                        if(ToastUtils.isInit())
                            ToastUtils.show("未授予權限")
                    }
                })
        }

        calculatorViewBinding.btnClear.setOnClickListener {
            calculatorViewBinding.result.setText("0")
        }
    }
}