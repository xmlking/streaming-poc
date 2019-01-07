package org.sumo.klogs

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Serialized
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import java.util.*


@SpringBootApplication
class RawWorkCountApplication

fun main(args: Array<String>) {
    SpringApplication.run(RawWorkCountApplication::class.java, *args)
}

@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaStreamsConfiguration(
    @Value(value = "\${app.kafka.topic.input}") private val inputTopic: String,
    @Value(value = "\${app.kafka.topic.output}") private val outputTopic: String,
    @Value(value = "\${schema.registry.url}") private val schemaRegistryUrl: String,
    private val kafkaProps: KafkaProperties,
    private val mapper: ObjectMapper) {

    val timeWindows = TimeWindows.of(5000)
    private val storeName = "for-WordCounts"
    val stringSerde = Serdes.String()

    private val log = LoggerFactory.getLogger(KafkaStreamsConfiguration::class.java)

    @Bean(name = arrayOf(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME))
    fun kStreamsConfigs(): StreamsConfig {
        val props = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "raw-count",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProps.bootstrapServers,
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
            //StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to  JsonSerde::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG to LogAndContinueExceptionHandler::class.java,
            StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG to WallclockTimestampExtractor::class.java
        )
        return StreamsConfig(props)
    }

    @Bean
    fun kStream(streamBuilder: StreamsBuilder): KStream<String, String> {
        val input: KStream<String, String> = streamBuilder.stream(inputTopic)

        if (log.isDebugEnabled) {
            input.print()
        }

        input
            .flatMapValues { value -> Arrays.asList(*value.toLowerCase().split("\\W+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) }
            .map { _, value -> KeyValue(value, value) }
            .groupByKey(Serialized.with(Serdes.String(), Serdes.String()))
            .windowedBy(timeWindows)
            .count(Materialized.`as`(storeName))
            .toStream()
            .map { key, value -> KeyValue<Any, WordCount>(null, WordCount(key.key(), value, Date(key.window().start()), Date(key.window().end()))) }
            .mapValues { value -> mapper.writeValueAsString(value)}
            .to(outputTopic)

        return input
    }

}


