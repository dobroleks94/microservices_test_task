FROM openjdk:17-alpine

COPY . /usr/app/
WORKDIR /usr/app/
EXPOSE 8010

RUN mv target/*.jar gateway.jar

CMD java -jar gateway.jar
