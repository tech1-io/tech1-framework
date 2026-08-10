FROM eclipse-temurin:21.0.11_10-jdk
WORKDIR /app
ARG PORT=3003
ENV PORT=${PORT}
EXPOSE ${PORT}
ADD /target/app.jar app.jar
CMD java ${JVM_ARGUMENTS} -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar app.jar
