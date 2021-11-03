package tw.wtb.floatcalculator

import android.annotation.SuppressLint
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
import net.objecthunter.exp4j.ExpressionBuilder
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

    @SuppressLint("SetTextI18n")
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
                } else {
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

            // 清除
            setOnClickListener(R.id.btnClear) { _, _ ->
                etResult.setText(R.string.btn_0)
                mLastNumeric = false
                mStateError = false
                mLastDot = false
            }

            // 刪除
            /*
            *  已知bug：
            *  假設輸入"2.6*"，刪除"*"後因當時輸入過"*"而導致mLastDot被初始化，
            *  因此小數點變成可重複輸入
            */
            setOnClickListener(R.id.btnDelete) { _, _ ->
                if(etResult.text.length == 1) {
                    etResult.setText(R.string.btn_0)
                    mLastNumeric = false
                    mStateError = false
                    mLastDot = false
                } else if(etResult.text.toString() != "0") {
                    if(etResult.text.last() == '/' || etResult.text.last() == '*' ||
                        etResult.text.last() == '-' || etResult.text.last() == '+') {
                        mLastNumeric = true
                    } else if(etResult.text.last() == '.') {
                        mLastNumeric = true
                        mLastDot = false
                    }

                    etResult.setText(etResult.text.dropLast(1))

                    if(etResult.text.last() == '/' || etResult.text.last() == '*' ||
                        etResult.text.last() == '-' || etResult.text.last() == '+') {
                        mLastNumeric = false
                    } else if(etResult.text.last() == '.') {
                        mLastNumeric = false
                        mLastDot = true
                    }
                }
            }

            // 數字
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

            // 小數點
            setOnClickListener(R.id.btnDot) { _, _ ->
                if (mLastNumeric && !mStateError && !mLastDot) {
                    etResult.append(".")
                    mLastNumeric = false
                    mLastDot = true
                }
            }

            // 運算符號
            setOnClickListener(R.id.btnPlus) { _, btn ->
                onOperator((btn as Button))
            }
            setOnClickListener(R.id.btnMinus) { _, btn ->
                onOperator((btn as Button))
            }
            setOnClickListener(R.id.btnTimes) { _, btn ->
                onOperator((btn as Button))
            }
            setOnClickListener(R.id.btnDivision) { _, btn ->
                onOperator((btn as Button))
            }

            // 開始計算
            setOnClickListener(R.id.btnEquals) { _, _ ->
                if (mLastNumeric && !mStateError) {
                    val txt = etResult.text.toString()
                    val expression = ExpressionBuilder(txt).build()

                    try {
                        val result = expression.evaluate()
                        etResult.setText(result.toString())
                        mLastDot = true // 計算結果為Double型態，以後再新增判斷移除多餘的0和小數點
                    } catch (ex: ArithmeticException) {
                        etResult.setText("Error!")
                        mStateError = true
                        mLastNumeric = false
                    }
                }
            }
        }
    }

    // 數字
    private fun onNumeric(btn: Button) {
        if (mStateError) {
            etResult.setText(btn.text)
            mStateError = false
        } else {
            if(etResult.text.toString() == "0") {
                etResult.setText(btn.text)
            } else {
                etResult.append(btn.text)
            }
        }
        mLastNumeric = true
    }

    // 運算符號
    private fun onOperator(btn: Button) {
        if (mLastNumeric && !mStateError) {
            etResult.append(btn.text)
            mLastNumeric = false
            mLastDot = false
        }
    }
}