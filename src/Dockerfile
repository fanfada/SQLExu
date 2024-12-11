FROM openjdk:8-jdk
MAINTAINER fanfada
ENV TZ="Asia/Shanghai"
ADD target/SQLExu.jar /app/myapp.jar
EXPOSE 8080
# 运行jar包
ENTRYPOINT ["java", "-Xms128m","-Xmx256m","-XX:MetaspaceSize=128M","-XX:MaxMetaspaceSize=256M","-jar", "/app/myapp.jar"]