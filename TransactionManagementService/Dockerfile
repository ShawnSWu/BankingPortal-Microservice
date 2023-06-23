FROM openjdk:11
MAINTAINER swshawnwu@gmail.com
COPY ./build/libs/TransactionManagementService.jar /TransactionManagementService.jar
EXPOSE 8082
ENV SPRING_PROFILES_ACTIVE=prd
CMD java -jar TransactionManagementService.jar