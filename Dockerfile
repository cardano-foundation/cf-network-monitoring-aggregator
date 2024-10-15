#FROM openjdk:21-jdk-slim
#ADD ./build/libs/*.jar /app/app.jar
#WORKDIR /app
#ENTRYPOINT ["java", "-jar", "app.jar"]


FROM openjdk:21-jdk-slim

WORKDIR /build

#RUN apt update --fix-missing \
#    && apt install -y --no-install-recommends openjdk-21-jdk maven curl \
#    && apt clean
#
#COPY ./pom.xml /build/pom.xml
#COPY ./src /build/src
#COPY ./.git .git

COPY . /build

RUN ls -lart

RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests


#WORKDIR /app
#RUN cp /build/target/*.jar /app/aggregator.jar
#RUN rm -rf /build
#
#ENTRYPOINT ["java", "-jar",  "/app/aggregator.jar"]
#
#CMD ["/bin/sh", "-c", "bash"]