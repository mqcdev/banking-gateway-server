FROM openjdk:11
VOLUME /tmp
EXPOSE 9090
ADD ./target/ms-gateway-0.0.1-SNAPSHOT.jar ms-gateway.jar
ENTRYPOINT ["java","-jar","/ms-gateway.jar"]