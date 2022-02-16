FROM openjdk:11-jre-slim
ADD target/libs/language-server-*-jar-with-dependencies.jar /app/sysml-language-server.jar
EXPOSE 3001
ENTRYPOINT ["java", "-jar", "/app/sysml-language-server.jar"]
CMD ["-sock", "-port", "3001"]
