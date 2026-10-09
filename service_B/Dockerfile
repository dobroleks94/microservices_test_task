FROM openjdk:17-alpine

COPY . /usr/app/
WORKDIR /usr/app/
EXPOSE 8030

RUN mv build/libs/*.jar serviceB.jar

CMD java -jar serviceB.jar
