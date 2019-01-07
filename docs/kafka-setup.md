Kafka Setup
===========
Kafka Production Setup playbook

### Environment

JAVA_HOME=/usr/java/jdk1.8.0_151

setup service account i.e., `streampoc` with Unix group i,e., `streampoc`

#### edge nodes
1. edge1
#### kafka nodes 
1. kafka1
2. kafka2
3. kafka3

### Installation 
> For each broker nodes (kafka1, kafka2, kafka3), ssh to the node and do following commands

```bash
ssh sumo@kafka1
cd /app
wget http://packages.confluent.io/archive/4.0/confluent-oss-4.0.0-2.11.zip
unzip confluent-oss-4.0.0-2.11.zip
chmod ug+rwx -R confluent-4.0.0
chmod o+rx -R confluent-4.0.0
ln -s confluent-4.0.0/ confluent
cd confluent
mkdir -p data
mkdir -p zkdata
# do on each node
touch 1 zkdata/myid
touch 2 zkdata/myid
touch 3 zkdata/myid
```

### Configuration  
edit `zookeeper.properties` and add:
```
server.1=kafka1:2888:3888
server.2=kafka2:2888:3888
server.3=kafka3:2888:3888

tickTime=2000
initLimit=5
syncLimit=2
autopurge.snapRetainCount=3
autopurge.purgeInterval=24
 ```

edit `server.properties` and add:
 ```
broker.id=0
broker.id=1
broker.id=2

delete.topic.enable=true
log.dirs=/app/confluent/data

zookeeper.connect=kafka1:2181,kafka2:2181,kafka3:2181
# zookeeper.connect=kafka2:2181,kafka1:2181,kafka3:2181
# zookeeper.connect=kafka3:2181,kafka2:2181,kafka1:2181

confluent.support.metrics.enable=false
```

### JMX
To enable JMX Monitoring for Kafka broker, please follow below instructions:
1. Edit `kafka-run-class` and modify `KAFKA_JMX_OPTS` variable like below (please replace `<-Djava.rmi.server.hostname>` with your Kafka Broker hostname)
    ```bash
    vi bin/kafka-run-class
    KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=kafka1 -Djava.net.preferIPv4Stack=true"
    ```
2. Add below line in `kafka-server-start`
    ```bash
    vi bin/kafka-server-start
    export JMX_PORT=${JMX_PORT:-9999}
    ```

### Start/Stop Kafka
```bash
# kafka start on kafka1,kafka2,kafka3
./bin/zookeeper-server-start -daemon etc/kafka/zookeeper.properties 
./bin/kafka-server-start -daemon etc/kafka/server.properties
./bin/connect-distributed -daemon etc/kafka/connect-distributed.properties
# kafka stop
./bin/zookeeper-server-stop 
./bin/kafka-server-stop

# kafka manager on kafka1
nohup ./bin/kafka-manager  > logs/kafka-manager.log 2>&1 &
```
 

### Kafka Manager 

####  Installation 

TODO
