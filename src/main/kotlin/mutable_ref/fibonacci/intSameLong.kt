package mutable_ref.fibonacci

import mutable_ref.MutableMfvcWrapper

fun fibonacciSame(n: Float): Float {
    val sharedWrapper = MutableMfvcWrapper()
    return when {
        n < 0 -> error("Wrong n: $n")
        n == 0.0f -> 0.0f
        else -> {
            fun impl(n: Float, sharedWrapper: MutableMfvcWrapper) {
                if (n == 1.0f) {
                    sharedWrapper.long0 = 0.0f.toRawBits().toLong().shl(32) or 1.0f.toRawBits().toLong().and(0xFFFFFFFF)
                } else {
                    impl(n - 1, sharedWrapper)
                    val x = Float.fromBits(sharedWrapper.long0.shr(32).toInt())
                    val y = Float.fromBits(sharedWrapper.long0.toInt())
                    sharedWrapper.long0 = y.toRawBits().toLong().shl(32) or (x + y).toRawBits().toLong().and(0xFFFFFFFF)
                }
            }
            impl(n, sharedWrapper)
            Float.fromBits(sharedWrapper.long0.toInt())
        }
    }
}

fun main() {
    for (i in 0..10) {
        println(fibonacciSame(i.toFloat()))
    }
}
