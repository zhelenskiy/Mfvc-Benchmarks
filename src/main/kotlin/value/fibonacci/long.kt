package value.fibonacci

@JvmInline
private value class DoublePair(val x: Double, val y: Double)
fun fibonacci(n: Double): Double = when {
    n < 0 -> error("Wrong n: $n")
    n == 0.0 -> 0.0
    else -> {
        fun impl(n: Double): DoublePair =
            if (n == 1.0) DoublePair(0.0, 1.0) else impl(n - 1).let { DoublePair(it.y, it.x + it.y) }
        impl(n).y
    }
}

fun main() {
    for (i in 0L..10L) {
        println(fibonacci(i.toDouble()))
    }
}
