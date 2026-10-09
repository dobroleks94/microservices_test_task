FROM openjdk:17-alpine

COPY . /usr/app/
WORKDIR /usr/app/
EXPOSE 8010

RUN mv build/libs/*.jar gateway.jar

CMD java -jar gateway.jar
