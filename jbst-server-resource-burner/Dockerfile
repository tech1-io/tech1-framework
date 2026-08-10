FROM eclipse-temurin:21.0.11_10-jdk
WORKDIR /app
EXPOSE 6002
ADD /target/app.jar app.jar
CMD java ${JVM_ARGUMENTS} -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-6002} -jar app.jar
