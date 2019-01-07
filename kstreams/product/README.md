Product
========
Product Kafka Streams App
 

### Run
> use `./gradlew` instead of `gradle` if you didn't installed `gradle`
```bash
gradle product:bootRun
```
### Test
```bash
gradle product:test
```
### Build
```bash
gradle product:build -x test 
# continuous build with `-t`. 
gradle -t product:build
# build docker image
gradle product:docker -x test 
```

### ssh to kafka container
```bash
docker-compose -f docker-compose-local.yml exec kafka bash
# then you can run following commands in this shell
```

### list topics
```bash
kafka-topics --list --zookeeper zookeeper:2181
```

### receive messages
```bash
kafka-console-consumer --bootstrap-server kafka:9092 \
--key-deserializer org.apache.kafka.common.serialization.IntegerDeserializer --property print.key=true --topic product-counts
```

### send messages
```bash
kafka-console-producer --broker-list kafka:9092 --topic products
 ```
 
 
Enter the following in the console producer (one line at a time) and watch the output on the console consumer:
```json 
{"id":"123"}
{"id":"124"}
{"id":"125"}
{"id":"123"}
{"id":"123"}
{"id":"123"}
 ```
