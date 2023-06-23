FROM openjdk:11
MAINTAINER swshawnwu@gmail.com
COPY ./build/libs/AuthService.jar /AuthService.jar
EXPOSE 9000
#ENV SPRING_PROFILES_ACTIVE=prd
CMD java -jar AuthService.jar