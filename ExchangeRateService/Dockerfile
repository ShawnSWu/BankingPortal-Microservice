FROM openjdk:11
MAINTAINER swshawnwu@gmail.com
COPY ./build/libs/ExchangeRateService.jar /ExchangeRateService.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev
CMD ./gradlew clean bootJar
CMD java -jar ExchangeRateService.jar