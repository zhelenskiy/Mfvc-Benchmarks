@file:Suppress("unused")

package microbenchmark

import shared.ComplexNumberConsumer
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class AckermannBenchmark {
    
    private class JmhComplexNumberConsumer(private val bh: Blackhole): ComplexNumberConsumer {
        override fun consume(real: Int, imaginary: Int) {
            bh.consume(real)
            bh.consume(imaginary)
        }

        override fun consume(real: Long, imaginary: Long) {
            bh.consume(real)
            bh.consume(imaginary)
        }
        override fun consume(real: Float, imaginary: Float) {
            bh.consume(real)
            bh.consume(imaginary)
        }

        override fun consume(real: Double, imaginary: Double) {
            bh.consume(real)
            bh.consume(imaginary)
        }
    }
    
    
    @Benchmark
    fun baselineInt(bh: Blackhole) = baseline.ackermann.heavyActionInt(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun baselineFloat(bh: Blackhole) = baseline.ackermann.heavyActionFloat(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun baselineLong(bh: Blackhole) = baseline.ackermann.heavyActionLong(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun baselineDouble(bh: Blackhole) = baseline.ackermann.heavyActionDouble(JmhComplexNumberConsumer(bh))


    @Benchmark
    fun longPackInt(bh: Blackhole) = long_pack.ackermann.heavyActionInt(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun longPackFloat(bh: Blackhole) = long_pack.ackermann.heavyActionFloat(JmhComplexNumberConsumer(bh))

    
    @Benchmark
    fun mutableUniversalRefIntIn2Longs(bh: Blackhole) = mutable_ref_universal.ackermann.heavyActionIntSeparate(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableUniversalRefFloatIn2Longs(bh: Blackhole) = mutable_ref_universal.ackermann.heavyActionFloatSeparate(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableUniversalRefIntIn1Long(bh: Blackhole) = mutable_ref_universal.ackermann.heavyActionIntSame(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableUniversalRefFloatIn1Long(bh: Blackhole) = mutable_ref_universal.ackermann.heavyActionFloatSame(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableUniversalRefLong(bh: Blackhole) = mutable_ref_universal.ackermann.heavyActionLong(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableUniversalRefDouble(bh: Blackhole) = mutable_ref_universal.ackermann.heavyActionDouble(JmhComplexNumberConsumer(bh))

    
    @Benchmark
    fun mutableSpecificRefInt(bh: Blackhole) = mutable_ref_specific.ackermann.heavyActionInt(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableSpecificRefFloat(bh: Blackhole) = mutable_ref_specific.ackermann.heavyActionFloat(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableSpecificRefLong(bh: Blackhole) = mutable_ref_specific.ackermann.heavyActionLong(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun mutableSpecificRefDouble(bh: Blackhole) = mutable_ref_specific.ackermann.heavyActionDouble(JmhComplexNumberConsumer(bh))

    
    @Benchmark
    fun valueInt(bh: Blackhole) = value.ackermann.heavyActionInt(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valueFloat(bh: Blackhole) = value.ackermann.heavyActionFloat(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valueLong(bh: Blackhole) = value.ackermann.heavyActionLong(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valueDouble(bh: Blackhole) = value.ackermann.heavyActionDouble(JmhComplexNumberConsumer(bh))

    
    @Benchmark
    fun valuePreserveBoxInt(bh: Blackhole) = value_preserve_box.ackermann.heavyActionInt(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valuePreserveBoxFloat(bh: Blackhole) = value_preserve_box.ackermann.heavyActionFloat(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valuePreserveBoxLong(bh: Blackhole) = value_preserve_box.ackermann.heavyActionLong(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valuePreserveBoxDouble(bh: Blackhole) = value_preserve_box.ackermann.heavyActionDouble(JmhComplexNumberConsumer(bh))


    @Benchmark
    fun valhallaJavaInt(bh: Blackhole) = valhalla.ackermann.java.IntKt.heavyActionInt(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valhallaJavaFloat(bh: Blackhole) = valhalla.ackermann.java.FloatKt.heavyActionFloat(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valhallaJavaLong(bh: Blackhole) = valhalla.ackermann.java.LongKt.heavyActionLong(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valhallaJavaDouble(bh: Blackhole) = valhalla.ackermann.java.DoubleKt.heavyActionDouble(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valhallaKotlinInt(bh: Blackhole) = valhalla.ackermann.kotlin.heavyActionInt(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valhallaKotlinFloat(bh: Blackhole) = valhalla.ackermann.kotlin.heavyActionFloat(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valhallaKotlinLong(bh: Blackhole) = valhalla.ackermann.kotlin.heavyActionLong(JmhComplexNumberConsumer(bh))

    @Benchmark
    fun valhallaKotlinDouble(bh: Blackhole) = valhalla.ackermann.kotlin.heavyActionDouble(JmhComplexNumberConsumer(bh))
}
