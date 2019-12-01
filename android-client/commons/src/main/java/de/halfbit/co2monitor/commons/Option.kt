package de.halfbit.co2monitor.commons

sealed class Option<out T : Any> {
    override operator fun equals(other: Any?): Boolean =
        when {
            this === None && other === None -> true
            this is Some && other is Some<*> -> value == other.value
            else -> false
        }

    override fun hashCode(): Int = when (this) {
        None -> javaClass.hashCode()
        is Some -> value.hashCode()
    }

    abstract val optional: T?
    abstract operator fun component1(): T?
    abstract fun <R : Any> let(block: (value: T) -> R): R?
}

object None : Option<Nothing>() {
    override val optional: Nothing? = null
    override fun component1(): Nothing? = null
    override fun <R : Any> let(block: (value: Nothing) -> R): R? = null
}

data class Some<out T : Any>(val value: T) : Option<T>() {
    override val optional: T? = value
    override fun <R : Any> let(block: (value: T) -> R): R? = block(value)
}

fun <T : Any> T?.toOption(): Option<T> =
    if (this == null) None else Some(
        this
    )