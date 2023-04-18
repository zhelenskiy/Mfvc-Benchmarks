package long_pack.fibonacci

@JvmInline
private value class FloatPair private constructor(private val storage: Long) {
    constructor(x: Float, y: Float) : this(x.toRawBits().toLong().shl(32) or y.toRawBits().toLong().and(0xFFFFFFFF))
    inline val x get() = Float.fromBits(storage.shr(32).toInt())
    inline val y get() = Float.fromBits(storage.toInt())
}
fun fibonacci(n: Float): Float = when {
    n < 0 -> error("Wrong n: $n")
    n == 0.0f -> 0.0f
    else -> {
        fun impl(n: Float): FloatPair =
            if (n == 1.0f) FloatPair(0.0f, 1.0f) else impl(n - 1).let { FloatPair(it.y, it.x + it.y) }
        impl(n).y
    }
}

fun main() {
    for (i in 0..10) {
        println(fibonacci(i.toFloat()))
    }
}
