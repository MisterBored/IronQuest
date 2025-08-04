FROM maven:3.9.6-eclipse-temurin-17 as maven

ADD pom.xml ./

RUN mvn -B -T 1C clean dependency:go-offline

ADD src ./src

RUN mvn -T 1C clean install

FROM eclipse-temurin:17-jre

RUN groupadd -r ironquest && useradd -r ironquest -g ironquest
USER ironquest:ironquest

WORKDIR /home/ironquest

COPY --from=maven target/*.jar ./

CMD ["/bin/sh", "-c", "java -jar /home/ironquest/*.jar --server.port=${PORT}"]
