## Dockerfile for Sprocket Engine
#
# CAUTION: You must provide env file and /mnt/logs volumes to work properly.
#
# example:
#  docker run --name=sprocket-engine-master -d --net=host --env-file=/path/to/sprocket-engine.env -e AKKA_PORT=2550 -v /path/to/logs:/mnt/logs sprocket-engine:VERSION master
#  docker run --name=sprocket-engine-agent  -d --net=host --env-file=/path/to/sprocket-engine.env -e AKKA_PORT=2551 -v /path/to/logs:/mnt/logs sprocket-engine:VERSION agent
#  docker run --name=sprocket-engine-worker -d --net=host --env-file=/path/to/sprocket-engine.env -e AKKA_PORT=2552 -v /path/to/logs:/mnt/logs sprocket-engine:VERSION worker
#

FROM java:7-jre
MAINTAINER Chris Kong <chris.kong.cn@gmail.com>

RUN mkdir -p /home/kong
ENV USER_HOME /home/kong

COPY target/universal/stage/bin $USER_HOME/bin
COPY target/universal/stage/lib $USER_HOME/lib
COPY /mnt/application.conf $USER_HOME/conf/application.conf

# symlink log directory
RUN ln -sfn /mnt/logs $USER_HOME/logs
RUN ln -sfn /mnt/config $USER_HOME/conf

ENV LANG C.UTF-8
WORKDIR _HOME
ENTRYPOINT [ "$USER_HOME/bin/scala-akka-loadbalance-pattern-with-docker" ]
