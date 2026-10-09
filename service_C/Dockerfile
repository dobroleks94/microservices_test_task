FROM openjdk:17-alpine

COPY . /usr/app/
WORKDIR /usr/app/
EXPOSE 8040

RUN mv build/libs/*.jar serviceC.jar

CMD java -jar serviceC.jar
