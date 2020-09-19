package kz.sozdik.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import kz.sozdik.R

private const val DEFAULT_FONT_SIZE = 14

class KazCharsView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs) {

    private val cyrillicChars = arrayOf('ә', 'і', 'ң', 'ғ', 'ү', 'ұ', 'қ', 'ө', 'һ')
    private val latinChars = arrayOf('á', 'i', 'ń', 'ǵ', 'ú', 'u', 'q', 'ó', 'h')
    private val chars: Array<Char> by lazy { if (type == Type.CYRILLIC) cyrillicChars else latinChars }

    var kazCharsClickListener: ((Char) -> Unit)? = null

    private var fontSize = DEFAULT_FONT_SIZE
    private var textColor = Color.WHITE

    var isAllCaps: Boolean = false
        set(value) {
            field = value
            (0 until childCount).forEach {
                val b = getChildAt(it) as TextView
                b.isAllCaps = isAllCaps
            }
        }

    var type: Type = Type.CYRILLIC
        set(value) {
            field = value
            val chars = when (type) {
                Type.CYRILLIC -> cyrillicChars
                Type.LATIN -> latinChars
            }
            children.forEachIndexed { index, view ->
                val textView = view as TextView
                textView.text = chars[index].toString()
            }
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.KazCharsView, defStyleAttr, defStyleRes)
        (0 until a.indexCount).forEach { i ->
            when (val attr = a.getIndex(i)) {
                R.styleable.KazCharsView_kcv_allCaps -> isAllCaps = a.getBoolean(attr, false)
                R.styleable.KazCharsView_kcv_fontSize -> fontSize = a.getDimensionPixelSize(attr, DEFAULT_FONT_SIZE)
                R.styleable.KazCharsView_kcv_textColor -> textColor = a.getColor(attr, Color.WHITE)
                R.styleable.KazCharsView_kcv_type -> type = Type.valueOf(a.getInt(attr, Type.CYRILLIC.type))!!
            }
        }
        a.recycle()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0, 0) {
        init()
    }

    @Suppress("MagicNumber")
    private fun init() {
        val density = context.resources.displayMetrics.density
        val paddingPixel = (8 * density).toInt()

        val marginPixel = density.toInt()
        val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
            leftMargin = marginPixel
            rightMargin = marginPixel
        }

        chars.forEach {
            TextView(context).apply {
                text = (if (this@KazCharsView.isAllCaps) it.toUpperCase() else it).toString()
                this.layoutParams = layoutParams
                setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.toFloat())
                setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel)
                setTextColor(textColor)
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.btn_black)
                setOnClickListener {
                    var char = (it as TextView).text[0]
                    char = if (this@KazCharsView.isAllCaps) char.toUpperCase() else char.toLowerCase()
                    kazCharsClickListener?.invoke(char)
                }
                addView(this)
            }
        }
    }

    enum class Type(val type: Int) {
        CYRILLIC(0),
        LATIN(1);

        companion object {
            fun valueOf(value: Int): Type? = values().find { it.type == value }
        }
    }
}