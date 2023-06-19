FROM openjdk:11
MAINTAINER swshawnwu@gmail.com
COPY ./build/libs/TransactionManagementService.jar /TransactionManagementService.jar
EXPOSE 8080
CMD ./gradlew clean build jar
CMD java -jar TransactionManagementService.jar