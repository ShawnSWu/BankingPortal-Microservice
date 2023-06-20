FROM openjdk:11
MAINTAINER swshawnwu@gmail.com
COPY ./build/libs/TransactionManagementService.jar /TransactionManagementService.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev
CMD java -jar TransactionManagementService.jar