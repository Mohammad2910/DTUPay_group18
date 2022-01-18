FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
CMD pwd
COPY target /usr/src
CMD ["java", "-Xmx64m", "-jar", "/usr/src/account-1.0-SNAPSHOT.jar"]