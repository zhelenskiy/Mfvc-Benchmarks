@file:Suppress("KotlinConstantConditions")

package value_preserve_box.fibonacci

@JvmInline
private value class FloatPair(val x: Float, val y: Float)
fun fibonacci(n: Float): Float = when {
    n < 0 -> error("Wrong n: $n")
    n == 0.0f -> 0.0f
    else -> {
        fun impl(n: Float): FloatPair = if (n == 1.0f) {
            var orNull: FloatPair? = null
            if (orNull == null) orNull = FloatPair(0.0f, 1.0f)
            orNull
        } else {
            var orNull: FloatPair? = null
            if (orNull == null) orNull = impl(n - 1).let { FloatPair(it.y, it.x + it.y) }
            orNull
        }
        impl(n).y
    }
}

fun main() {
    for (i in 0..10) {
        println(fibonacci(i.toFloat()))
    }
}
