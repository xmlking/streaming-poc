Count
=====
WorkCount Kafka Streams App

> use `./gradlew` instead of `gradle` if you didn't installed `gradle`

### Test
```bash
gradle count:test
```
### Build
```bash
gradle count:build -x test 
# continuous build with `-t`. 
gradle -t count:build
# build docker image
gradle count:docker -x test 
```

 ### Deploy
 > Deploying to production.
```bash
nohup java -jar -Dspring.profiles.active=prod count-0.1.0-SNAPSHOT.jar > count.log 2>&1 & 
```

### Run
> run locally
#### start kafka
```bash
docker-compose up kafka
docker-compose -f docker-compose-local.yml up
# stop and remove volumes
docker-compose -f docker-compose-local.yml down -v
```

#### start app
```bash
gradle count:bootRun
# run with `prod` profile.
SPRING_PROFILES_ACTIVE=prod gradle count:bootRun
# fource to enable debug logs
SPRING_PROFILES_ACTIVE=prod gradle count:bootRun --debug
# via docker
docker-compose up count
```

### ssh to kafka container
```bash
docker-compose -f docker-compose-local.yml exec kafka bash
# then you can run following commands in this shell
```

### receive messages
```bash
kafka-console-consumer --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic counts
```

### send messages
```bash
kafka-console-producer --broker-list kafka:9092 --topic words
```
