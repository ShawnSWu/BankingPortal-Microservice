FROM openjdk:11
MAINTAINER swshawnwu@gmail.com
COPY ./build/libs/ApiGatewayService.jar /ApiGatewayService.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev
CMD java -jar ApiGatewayService.jar