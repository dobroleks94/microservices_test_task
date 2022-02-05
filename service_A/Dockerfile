FROM openjdk:17-alpine

COPY . /usr/app/
WORKDIR /usr/app/
EXPOSE 8020

RUN mv target/*.jar serviceA.jar

CMD java -jar serviceA.jar
