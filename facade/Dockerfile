FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
COPY target/quarkus-app /usr/src/quarkus-app
#CMD java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 quarkus-app/quarkus-run.jar
CMD java -jar quarkus-app/quarkus-run.jar