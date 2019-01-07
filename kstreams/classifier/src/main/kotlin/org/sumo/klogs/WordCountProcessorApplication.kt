@file:JvmName("WordCountProcessorApplicationKt")

package org.sumo.klogs

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.kstream.internals.TimeWindow
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.handler.annotation.SendTo
import java.util.*


@SpringBootApplication
@EnableBinding(KStreamProcessorWithBranches::class)
class WordCountProcessorApplication

fun main(args: Array<String>) {
    SpringApplication.run(WordCountProcessorApplication::class.java, *args)
}
//todo https://github.com/spring-cloud/spring-cloud-stream-samples/blob/master/processor-samples/reactive-processor/src/main/java/reactive/kafka/ReactiveProcessorApplication.java

@Configuration
class WorkCountProcessor(
    val timeWindows: TimeWindows,
    @Value(value = "\${app.kafka.store-name:for-WordCounts}") private val storeName: String) {

    @StreamListener("input")
    @SendTo("output1", "output2", "output3")
    fun process(input: KStream<Any, String>): Array<KStream<Any, WordCount>> {

        val isEnglish = Predicate { _: Any?, v: WordCount -> v.word == "english" }
        val isFrench = Predicate { _: Any?, v: WordCount -> v.word == "french" }
        val isSpanish = Predicate { _: Any?, v: WordCount -> v.word == "spanish" }

        return input
            .flatMapValues { value -> Arrays.asList(*value.toLowerCase().split("\\W+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) }
            .map { _, value -> KeyValue(value, value) }
            .groupByKey(Serialized.with(Serdes.String(), Serdes.String()))
            .windowedBy<TimeWindow>(timeWindows)
            .count(Materialized.`as`(storeName))
            .toStream()
            .map { key, value -> KeyValue<Any, WordCount>(null, WordCount(key.key(), value, Date(key.window().start()), Date(key.window().end()))) }
            .branch(isEnglish, isFrench, isSpanish)
    }
}


internal interface KStreamProcessorWithBranches {
    @Input("input")
    fun input(): KStream<*, *>

    @Output("output1")
    fun output1(): KStream<*, *>

    @Output("output2")
    fun output2(): KStream<*, *>

    @Output("output3")
    fun output3(): KStream<*, *>
}


