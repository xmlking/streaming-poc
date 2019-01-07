Raw-Count
=========
Raw Count Kafka Streams App

> use `./gradlew` instead of `gradle` if you didn't installed `gradle`

### Test
```bash
gradle raw-count:test
```
### Build
```bash
gradle raw-count:build -x test 
# continuous build with `-t`. 
gradle -t raw-count:build
# build docker image
gradle raw-count:docker -x test 
```
 
 ### Deploy
 > Deploying to production.  
 ```bash
 nohup java -jar -Dspring.profiles.active=prod raw-count-0.1.0-SNAPSHOT.jar > raw-count.log 2>&1 & 
 ```
 
### Run
> run locally
#### start kafka
```bash
docker-compose up
# stop and remove volumes
docker-compose down -v
```

#### start app
```bash
gradle raw-count:bootRun
# run with `prod` profile.
SPRING_PROFILES_ACTIVE=prod gradle raw-count:bootRun
# fource to enable debug logs
SPRING_PROFILES_ACTIVE=prod gradle raw-count:bootRun --debug
```

### ssh to kafka container
```bash
docker-compose exec kafka bash
# then you can run following commands in this shell
```

### list topics
```bash
kafka-topics --zookeeper zookeeper:2181 --list
```

### to delete topics
```bash
kafka-topics --zookeeper localhost:2181 --delete --topic counts
```

### receive messages
```bash
kafka-console-consumer --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic counts
```

### send messages
```bash
kafka-console-producer --broker-list kafka:9092 --topic words
```
