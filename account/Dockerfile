FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
CMD pwd
RUN cp target/account-1.0-SNAPSHOT.jar /usr/src/account-1.0-SNAPSHOT.jar
CMD ["java", "-Xmx64m", "-jar", "/usr/src/account-1.0-SNAPSHOT.jar"]