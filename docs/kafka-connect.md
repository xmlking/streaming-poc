Kafka
=====
Kafka, ZooKeeper, Kafka-Manager, Kafka Connector, Kafka Streams, Kafka Schema Registry 

### Install
> install kafka CLI tools locally.
```bash
cd /Developer/Applications/
curl http://packages.confluent.io/archive/4.0/confluent-oss-4.0.0-2.11.zip | tar xz
```

### Kafka Manager
http://kafka1:9000/clusters/poc/topics

### List Topics
```bash
/Developer/Applications/confluent-4.0.0/bin/kafka-topics  --zookeeper kafka1,kafka2,kafka3 --list
```

### Create Topics
```bash
/Developer/Applications/confluent-4.0.0/bin/kafka-topics --create --zookeeper kafka1,kafka2,kafka3  --replication-factor 3 --partitions 3 --topic app_logs
/Developer/Applications/confluent-4.0.0/bin/kafka-topics --create --zookeeper kafka1,kafka2,kafka3  --replication-factor 3 --partitions 3 --topic peg_logs
/Developer/Applications/confluent-4.0.0/bin/kafka-topics --create --zookeeper kafka1,kafka2,kafka3  --replication-factor 3 --partitions 3 --topic call_logs
/Developer/Applications/confluent-4.0.0/bin/kafka-topics --create --zookeeper kafka1,kafka2,kafka3  --replication-factor 3 --partitions 3 --topic error_logs

/Developer/Applications/confluent-4.0.0/bin/kafka-topics --create --zookeeper kafka1,kafka2,kafka3  --replication-factor 1 --partitions 1 --topic app_logs_duplicate
/Developer/Applications/confluent-4.0.0/bin/kafka-configs --zookeeper kafka1,kafka2,kafka3 \
--entity-type topics --alter --add-config retention.ms=1000 --entity-name app_logs_duplicate
```

### Delete Topics
```bash
/Developer/Applications/confluent-4.0.0/bin/kafka-topics --zookeeper kafka1,kafka2,kafka3 \
--delete --topic peg_logs
```

### Receive Messages
```bash
/Developer/Applications/confluent-4.0.0/bin/kafka-console-consumer \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --from-beginning --property print.key=true --topic app_logs
/Developer/Applications/confluent-4.0.0/bin/kafka-console-consumer \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --from-beginning --property print.key=true --topic peg_logs
/Developer/Applications/confluent-4.0.0/bin/kafka-console-consumer \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --from-beginning --property print.key=true --topic call_logs
/Developer/Applications/confluent-4.0.0/bin/kafka-console-consumer \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --from-beginning --property print.key=true --topic error_logs
/Developer/Applications/confluent-4.0.0/bin/kafka-console-consumer \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --from-beginning --property print.key=true --topic app_logs_duplicate
```

### Send Messages
```bash
/Developer/Applications/confluent-4.0.0/bin/kafka-console-producer \
--broker-list kafka1:9092,kafka2:9092,kafka3:9092 --topic app_logs
```

### Purge Kafka Topic
```bash
# Temporarily update the retention time on the topic to one second:
/Developer/Applications/confluent-4.0.0/bin/kafka-configs --zookeeper kafka1,kafka2,kafka3 \
--entity-type topics --alter --add-config retention.ms=1000 --entity-name app_logs
 
# wait until topic is pruned (this command should not show any messages)
/Developer/Applications/confluent-4.0.0/bin/kafka-console-consumer \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --from-beginning --property print.key=true --topic app_logs

# Then set it back to default
/Developer/Applications/confluent-4.0.0/bin/kafka-configs --zookeeper kafka1,kafka2,kafka3 \
--entity-type topics --alter --delete-config retention.ms --entity-name app_logs
# Check if it is back to default
/Developer/Applications/confluent-4.0.0/bin/kafka-configs --zookeeper kafka1,kafka2,kafka3 \
--entity-type topics --describe --entity-name app_logs
```

### Reset Offsets
```bash
# list consumer-groups
/Developer/Applications/confluent-4.0.0/bin/kafka-consumer-groups \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --list
/Developer/Applications/confluent-4.0.0/bin/kafka-consumer-groups \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --list | grep connector

# see offsets for `analyzer` consumer-group or for e.g., `connect-peg-logs-connector`
/Developer/Applications/confluent-4.0.0/bin/kafka-consumer-groups \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --describe --group analyzer

# make sure the `analyzer` app is stoped, then run to reset offsets:
/Developer/Applications/confluent-4.0.0/bin/kafka-consumer-groups \
--bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092  --group analyzer \
--reset-offsets --to-earliest --all-topics --execute
```
### Connector

REST API [Reference](https://docs.confluent.io/current/connect/restapi.html)

> List the connector plugins available on this worker
```bash
curl kafka1:8083/connector-plugins
```
> Listing active connectors on a worker 
```bash
curl http://kafka1:8083/connectors
```
> Getting connector configuration
```bash
curl http://kafka1:8083/connectors/app-logs-connector
```
> Getting tasks for a connector
```bash
curl http://kafka1:8083/connectors/app-logs-connector/tasks
```
> Getting connector status
```bash
curl http://kafka1:8083/connectors/app-logs-connector/status
```
> Restarting a connector
```bash
curl -XPOST 'kafka1:8083/connectors/app-logs-connector/restart'
```
> Restarting a task
```bash
curl -XPOST 'kafka1:8083/connectors/app-logs-connector/tasks/0/restart'
```
> Pausing a connector (useful if downtime is needed for the system the connector interacts with)
```bash
curl -XPUT 'kafka1:8083/connectors/app-logs-connector/pause'
```
> Resuming a connector
```bash
curl -XPUT 'kafka1:8083/connectors/app-logs-connector/resume'
```
> Deleting a connector
```bash
curl -XDELETE 'kafka1:8083/connectors/app-logs-connector'
```
> Create Connector for Prod
```bash
# App Logs
curl -XPOST -H 'Content-type:application/json' 'http://kafka1:8083/connectors' -d '{
    "name": "app-logs-connector",
    "config": {
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
        "key.converter.schemas.enable": "false",
        "value.converter.schemas.enable": "false",
        "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
        "tasks.max" : "1",
        "topics": "app_logs",
        "connection.url":"http://kafka1:9200,http://kafka2:9200,http://kafka3:9200",
        "type.name" : "app",
        "key.ignore" : "true",
        "schema.ignore" : "true"
    }
}'

# Peg Logs
curl -XPOST -H 'Content-type:application/json' 'http://kafka1:8083/connectors' -d '{
    "name": "peg-logs-connector",
    "config": {
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
        "key.converter.schemas.enable": "false",
        "value.converter.schemas.enable": "false",
        "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
        "tasks.max" : "1",
        "topics": "peg_logs",
        "connection.url":"http://kafka1:9200,http://kafka2:9200,http://kafka3:9200",
        "type.name" : "peg",
        "key.ignore" : "false",
        "schema.ignore" : "true"
    }
}'

# Call Logs
curl -XPOST -H 'Content-type:application/json' 'http://kafka1:8083/connectors' -d '{
    "name": "call-logs-connector",
    "config": {
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
        "key.converter.schemas.enable": "false",
        "value.converter.schemas.enable": "false",
        "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
        "tasks.max" : "1",
        "topics": "call_logs",
        "connection.url":"http://kafka1:9200,http://kafka2:9200,http://kafka3:9200",
        "type.name" : "call",
        "key.ignore" : "false",
        "schema.ignore" : "true"
    }
}'

# Error Logs
curl -XPOST -H 'Content-type:application/json' 'http://kafka1:8083/connectors' -d '{
    "name": "error-logs-connector",
    "config": {
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
        "key.converter.schemas.enable": "false",
        "value.converter.schemas.enable": "false",
        "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
        "tasks.max" : "1",
        "topics": "error_logs",
        "connection.url":"http://kafka1:9200,http://kafka2:9200,http://kafka3:9200",
        "type.name" : "error",
        "key.ignore" : "false",
        "schema.ignore" : "true"
    }
}'
```

> Create Connector for Docker
```bash
# create elasticsearch connector
# Peg Logs
curl -XPOST -H 'Content-type:application/json' 'localhost:8083/connectors' -d '{
 "name": "peg-logs-connector",
 "config" : {
  "key.converter": "org.apache.kafka.connect.storage.StringConverter",
  "key.converter.schemas.enable": "false",
  "value.converter.schemas.enable": "false",
  "connector.class" : "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
  "tasks.max" : "1",
  "topics" : "peg_logs", 
  "connection.url" : "http://elasticsearch:9200",
  "type.name" : "peg",
  "key.ignore" : "false",
  "schema.ignore" : "true"
 }
}'

# Call Logs
curl -XPOST -H 'Content-type:application/json' 'localhost:8083/connectors' -d '{
 "name": "call-logs-connector",
 "config" : {
  "key.converter": "org.apache.kafka.connect.storage.StringConverter",
  "key.converter.schemas.enable": "false",
  "value.converter.schemas.enable": "false",
  "connector.class" : "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
  "tasks.max" : "1",
  "topics" : "call_logs", 
  "connection.url" : "http://elasticsearch:9200",
  "type.name" : "call",
  "key.ignore" : "false",
  "schema.ignore" : "true"
 }
}'

# Error Logs
curl -XPOST -H 'Content-type:application/json' 'localhost:8083/connectors' -d '{
 "name": "error-logs-connector",
 "config" : {
  "key.converter": "org.apache.kafka.connect.storage.StringConverter",
  "key.converter.schemas.enable": "false",
  "value.converter.schemas.enable": "false",
  "connector.class" : "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
  "tasks.max" : "1",
  "topics" : "error_logs", 
  "connection.url" : "http://elasticsearch:9200",
  "type.name" : "error",
  "key.ignore" : "false",
  "schema.ignore" : "true"
 }
}'
```

### Schema Registry 
* http://kafka1:8081/
* http://kafka2:8081/
* http://kafka3:8081/
