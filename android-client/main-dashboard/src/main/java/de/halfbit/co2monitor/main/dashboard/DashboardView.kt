package de.halfbit.co2monitor.main.dashboard

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import de.halfbit.co2monitor.commons.Option
import de.halfbit.co2monitor.commons.StringRef
import io.reactivex.functions.Consumer
import magnet.Instance


interface DashboardView {
    val co2Text: Consumer<String>
    val co2Level: Consumer<Co2Level>
    val temperatureText: Consumer<String>
    val temperatureLevel: Consumer<TemperatureLevel>
    val loadingVisibility: Consumer<Boolean>
    val messageText: Consumer<Option<StringRef>>
}

sealed class Co2Level(@AttrRes val colorAttr: Int) {
    object Unknown : Co2Level(R.attr.colorLevel0)
    object Good : Co2Level(R.attr.colorLevel2)
    object Acceptable : Co2Level(R.attr.colorLevel3)
    object Bad : Co2Level(R.attr.colorLevel4)
}

sealed class TemperatureLevel(@AttrRes val colorAttr: Int) {
    object Unknown : TemperatureLevel(R.attr.colorLevel0)
    object Hot : TemperatureLevel(R.attr.colorLevel3)
    object Good : TemperatureLevel(R.attr.colorLevel2)
    object Cold : TemperatureLevel(R.attr.colorLevel1)
}

@Instance(type = DashboardView::class)
internal class DefaultDashboardView(
    root: View,
    private val context: Context
) : DashboardView {

    private val co2 = Section(root.findViewById(R.id.co2))
    private val temperature = Section(root.findViewById(R.id.temperature))
    private val loading = root.findViewById<ProgressBar>(R.id.progress)
    private val message = root.findViewById<TextView>(R.id.message)

    init {
        with(co2) {
            title.setText(R.string.co2_title)
            icon.setImageResource(R.drawable.ic_scatter_plot_24px)
        }
        with(temperature) {
            title.setText(R.string.temperature_title)
            icon.setImageResource(R.drawable.ic_thermometer_24px)
        }
    }

    override val co2Text: Consumer<String> =
        Consumer { co2.value.text = it }

    override val co2Level: Consumer<Co2Level> =
        Consumer { co2.background.setColor(context.getAttrColor(it.colorAttr)) }

    override val temperatureText: Consumer<String> =
        Consumer { temperature.value.text = it }

    override val temperatureLevel: Consumer<TemperatureLevel> =
        Consumer { temperature.background.setColor(context.getAttrColor(it.colorAttr)) }

    override val loadingVisibility: Consumer<Boolean> =
        Consumer { loading.setVisible(it) }

    override val messageText: Consumer<Option<StringRef>> =
        Consumer { message.text = it.let { text -> text.resolve(message.resources) } }

    private class Section(root: View) {
        val title: TextView = root.findViewById(R.id.title)
        val value: TextView = root.findViewById(R.id.value)
        val icon: ImageView = root.findViewById(R.id.icon)
        val background = root.background as GradientDrawable
    }
}

@ColorInt
private fun Context.getAttrColor(@AttrRes colorAttr: Int): Int =
    TypedValue().let {
        theme.resolveAttribute(colorAttr, it, false)
        it.data
    }

private fun View.setVisible(visible: Boolean, invisible: Int = View.GONE) {
    visibility = if (visible) View.VISIBLE else invisible
}
