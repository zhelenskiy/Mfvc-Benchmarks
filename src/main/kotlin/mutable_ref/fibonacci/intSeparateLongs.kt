package mutable_ref.fibonacci

import mutable_ref.MutableMfvcWrapper

fun fibonacciSeparate(n: Float): Float {
    val sharedWrapper = MutableMfvcWrapper()
    return when {
        n < 0 -> error("Wrong n: $n")
        n == 0.0f -> 0.0f
        else -> {
            fun impl(n: Float, sharedWrapper: MutableMfvcWrapper) {
                if (n == 1.0f) {
                    sharedWrapper.long0 = 0.0f.toRawBits().toLong()
                    sharedWrapper.long1 = 1.0f.toRawBits().toLong()
                } else {
                    impl(n - 1, sharedWrapper)
                    val x = Float.fromBits(sharedWrapper.long0.toInt())
                    val y = Float.fromBits(sharedWrapper.long1.toInt())
                    sharedWrapper.long0 = y.toRawBits().toLong()
                    sharedWrapper.long1 = (x + y).toRawBits().toLong()
                }
            }
            impl(n, sharedWrapper)
            Float.fromBits(sharedWrapper.long1.toInt())
        }
    }
}

fun main() {
    for (i in 0..10) {
        println(fibonacciSeparate(i.toFloat()))
    }
}
