FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
COPY target/quarkus-app /usr/src/quarkus-app
CMD ["java", "-Xmx64m", "-jar", "/quarkus-run.jar"]