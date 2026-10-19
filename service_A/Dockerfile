FROM eclipse-temurin:17-jdk-jammy

COPY . /usr/app/
WORKDIR /usr/app/
EXPOSE 8020

RUN mv build/libs/*.jar serviceA.jar

CMD java -jar serviceA.jar
