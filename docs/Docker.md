Docker
======
Docker Cheat Sheet.

### Install
Install `Docker for Mac` app [Installation](https://docs.docker.com/docker-for-mac/install/)
> Give Docker Engine at least 8GB RAM and 4 CPU Cores for running Hadoop components

### Docker Commands

```bash
docker info
# To see list of images
docker images
docker images -a
# To delete an image
docker rmi  eb46b3df6e36 
docker rmi  eb46b3df6e36 -f
# To run an image 
docker run -p 8081:8081 -i -t reactive/mongo-data-service:0.1.0-SNAPSHOT
docker run -p 8082:8082 -it reactive/stream-service:0.1.0-SNAPSHOT
docker start -p 8082:8082 -it reactive/stream-service:0.1.0-SNAPSHOT
docker run -p 8080:8080 -e "app.mongoApiUrl=http://localhost:8081"  -e "app.streamApiUrl=http://localhost:8082" -i -t reactive/ui-app:0.1.0-SNAPSHOT
# Using Spring Profiles
$ docker run -e "SPRING_PROFILES_ACTIVE=prod" -p 8082:8082 -i -t reactive/stream-service:0.1.0-SNAPSHOT
# Debugging the application in a Docker container
$ docker run -e "JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" -p 8082:8082 -p 5005:5005 -i -t reactive/stream-service:0.1.0-SNAPSHOT
# To see list of running containers
docker ps
# To stop a running container
docker stop 81c723d22865
# SSH to the running container (CONTAINER ID from `docker ps` command)
docker exec -i <CONTAINER ID> sh
```

##### Docker Compose
```bash
# start containers in the background
docker-compose up -d
# start containers in the foreground
docker-compose up 
# show runnning containers 
docker-compose ps
# scaling containers and load balancing
docker-compose scale stream=3
# 1. stop the running containers using
docker-compose stop
# 2. remove the stopped containers using
docker-compose rm -f
```

### Maintenance
```bash
docker container prune
docker image prune
docker network prune
docker volume prune
# will delete ALL unused data (i.e. In order: containers stopped, volumes without containers and images with no containers).
docker system prune
# find open ports
netstat -ap tcp | grep -i "listen"
```
