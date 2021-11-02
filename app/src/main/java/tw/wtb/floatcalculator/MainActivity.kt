package tw.wtb.floatcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
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

    private val etResult by lazy { mCalculatorView.findViewById<EditText>(R.id.etResult) }

    // 用於判斷最後一個輸入的字串是否為數字
    private var mLastNumeric: Boolean = false
    // 用於記錄計算狀態是否錯誤
    private var mStateError: Boolean = false
    // 如果為真，則不可再添加小數點
    private var mLastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 啟動懸浮圖標
        binding.btnStart.setOnClickListener {
            // 取得權限
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

        // 懸浮圖標
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

        // 計算機
        mCalculatorView = XToast<XToast<*>>(application).apply {
            setContentView(R.layout.calculator_view)

            // 清除鍵
            setOnClickListener(R.id.btnClear) { _, _ ->
                etResult.setText(R.string.et_clear)
                mLastNumeric = false
                mStateError = false
                mLastDot = false
            }

            // 刪除鍵
            setOnClickListener(R.id.btnDelete) { _, _ ->
                if(etResult.text.length == 1) {
                    etResult.setText(R.string.et_clear)
                    mLastNumeric = false
                    mStateError = false
                    mLastDot = false
                }
                else if(etResult.text.toString() != "0") {
                    etResult.setText(etResult.text.dropLast(1))
                    // mLastNumeric、mStateError、mLastDot狀態尚未判斷
                    
                }
            }

            // 數字鍵
            setOnClickListener(R.id.btn0) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn1) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn2) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn3) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn4) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn5) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn6) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn7) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn8) { _, btn ->
                onNumeric((btn as Button))
            }
            setOnClickListener(R.id.btn9) { _, btn ->
                onNumeric((btn as Button))
            }
        }
    }

    private fun onNumeric(btn: Button) {
        if (mStateError) {
            // If current state is Error, replace the error message
            etResult.setText(btn.text)
            mStateError = false
        }
        else {
            if(etResult.text.toString() == "0") {
                etResult.setText(btn.text)
            }
            else {
                etResult.append(btn.text)
            }
        }
        // Set the flag
        mLastNumeric = true
    }
}