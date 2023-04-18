package mutable_ref.fibonacci

import mutable_ref.MutableMfvcWrapper

fun fibonacci(n: Double): Double {
    val sharedWrapper = MutableMfvcWrapper()
    return when {
        n < 0 -> error("Wrong n: $n")
        n == 0.0 -> 0.0
        else -> {
            fun impl(n: Double, sharedWrapper: MutableMfvcWrapper) {
                if (n == 1.0) {
                    sharedWrapper.long0 = 0.0.toRawBits()
                    sharedWrapper.long1 = 1.0.toRawBits()
                } else {
                    impl(n - 1, sharedWrapper)
                    val x = Double.fromBits(sharedWrapper.long0)
                    val y = Double.fromBits(sharedWrapper.long1)
                    sharedWrapper.long0 = y.toRawBits()
                    sharedWrapper.long1 = (x + y).toRawBits()
                }
            }
            impl(n, sharedWrapper)
            Double.fromBits(sharedWrapper.long1)
        }
    }
}

fun main() {
    for (i in 0L..10L) {
        println(fibonacci(i.toDouble()))
    }
}
