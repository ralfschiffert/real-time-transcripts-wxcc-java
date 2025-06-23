#FROM arm64v8/openjdk
FROM google/cloud-sdk

RUN mkdir /app

COPY target/dialog-connector-simulator-1.0.0-SNAPSHOT-allinone.jar /app

WORKDIR /app

EXPOSE 8086

CMD ["java", "-jar", "/app/dialog-connector-simulator-1.0.0-SNAPSHOT-allinone.jar"]

