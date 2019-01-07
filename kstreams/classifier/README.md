classifier
==========
classifier Kafka Streams App

> use `./gradlew` instead of `gradle` if you didn't installed `gradle`

### Test
```bash
gradle classifier:test
```
### Build
```bash
gradle classifier:build -x test 
# continuous build with `-t`. 
gradle -t classifier:build
# build docker image
gradle classifier:docker -x test 
```

 ### Deploy
 > Deploying to production.
```bash
nohup java -jar -Dspring.profiles.active=prod classifier-0.1.0-SNAPSHOT.jar > classifier.log 2>&1 & 
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
gradle classifier:bootRun
# run with `prod` profile.
SPRING_PROFILES_ACTIVE=prod gradle classifier:bootRun
# fource to enable debug logs
SPRING_PROFILES_ACTIVE=prod gradle classifier:bootRun --debug
# via docker
docker-compose up classifier
```

### ssh to kafka container
```bash
docker-compose -f docker-compose-local.yml exec kafka bash
# then you can run following commands in this shell
```

### receive messages
```bash
kafka-console-consumer --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic ecount
kafka-console-consumer --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic fcount
kafka-console-consumer --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic scount
```

### send messages
```bash
kafka-console-producer --broker-list kafka:9092 --topic words
```

Enter the following in the console producer (one line at a time) and watch the output on the console consumer:
```  
english
english
french
english
spanish
spanish
spanish
french
 ```
