package kz.sozdik.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import kz.sozdik.R

class TitledTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleTextView: TextView
    private val descriptionTextView: TextView

    init {
        var description: String? = ""
        var title: String? = ""

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitledTextView)
            description = typedArray.getString(R.styleable.TitledTextView_description)
            title = typedArray.getString(R.styleable.TitledTextView_title)
            typedArray.recycle()
        }

        inflate(context, R.layout.widget_titled_text_top, this)

        orientation = VERTICAL

        descriptionTextView = findViewById(R.id.descriptionView)
        titleTextView = findViewById(R.id.titleView)

        descriptionTextView.text = description
        titleTextView.text = title
    }

    var description: String
        get() = descriptionTextView.text.toString()
        set(value) {
            descriptionTextView.text = value
        }

    var title: String
        get() = titleTextView.text.toString()
        set(value) {
            titleTextView.text = value
            titleTextView.isVisible = value.isNotEmpty()
        }
}