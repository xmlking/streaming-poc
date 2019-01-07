package org.sumo.klogs

import java.time.Instant
import java.time.ZoneId

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.TimeWindows

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.binder.kstream.annotations.KStreamProcessor
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.messaging.handler.annotation.SendTo
import java.awt.SystemColor.window
import jdk.nashorn.tools.ShellFunctions.input
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Serialized
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.time.LocalTime


@SpringBootApplication
@EnableBinding(KStreamProcessor::class)
class KlogsApplication

fun main(args: Array<String>) {
    runApplication<KlogsApplication>(*args)
}

@Configuration
class KafkaProcessorConfiguration(val timeWindows: TimeWindows,
@Value(value = "\${app.kafka.store-name:product-counts}") private val storeName: String) {

    @StreamListener("input")
    @SendTo("output")
    fun process(input: KStream<Any, Product>): KStream<Int, ProductStatus> {

        return input
                .filter { _, product -> productIds().contains(product.id) }
                .map { _, value -> KeyValue(value, value) }
                .groupByKey(Serialized.with(JsonSerde(Product::class.java), JsonSerde(Product::class.java)))
                .windowedBy(timeWindows)
                .count(Materialized.`as`(storeName))

                .toStream()
                .map { key, value ->
                    KeyValue<Int, ProductStatus>(key.key().id, ProductStatus(key.key().id,
                            value, Instant.ofEpochMilli(key.window().start()).atZone(ZoneId.systemDefault()).toLocalTime(),
                            Instant.ofEpochMilli(key.window().end()).atZone(ZoneId.systemDefault()).toLocalTime()))
                }
    }

    private fun productIds(): Set<Int> {
        return setOf(123,124,125)
    }
}



