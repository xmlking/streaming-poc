package com.sumo.experiments

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext

fun main(args: Array<String>) {

    val conf = SparkConf()
        .setMaster("local[2]")
        .setAppName("Kotlin Spark Test")
        .set("spark.driver.host", "localhost")
    val sc = JavaSparkContext(conf)

    val items = listOf("123/643/7563/2134/ALPHA", "2343/6356/BETA/2342/12", "23423/656/343")

    val input = sc.parallelize(items)

    val sumOfNumbers = input.flatMap { it.split("/").iterator() }
        .filter { it.matches(Regex("[0-9]+")) }
        .map { it.toInt() }
        .reduce {total,next -> total + next }

    println(sumOfNumbers)
}
