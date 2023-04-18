@file:Suppress("KotlinConstantConditions")

package value_preserve_box.fibonacci

@JvmInline
private value class DoublePair(val x: Double, val y: Double)
fun fibonacci(n: Double): Double = when {
    n < 0 -> error("Wrong n: $n")
    n == 0.0 -> 0.0
    else -> {
        fun impl(n: Double): DoublePair = if (n == 1.0) {
            var orNull: DoublePair? = null
            if (orNull == null) orNull = DoublePair(0.0, 1.0)
            orNull
        } else {
            var orNull: DoublePair? = null
            if (orNull == null) orNull = impl(n - 1).let { DoublePair(it.y, it.x + it.y) }
            orNull
        }
        impl(n).y
    }
}

fun main() {
    for (i in 0L..10L) {
        println(fibonacci(i.toDouble()))
    }
}
