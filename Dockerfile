FROM eclipse-temurin:21-jre

WORKDIR /

COPY [ "./target/*.jar", "/tng-key-distribution.jar" ]

ENV JAVA_OPTS="$JAVA_OPTS -Xms256M -Xmx1G"

EXPOSE 8080

USER 65534:65534

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /tng-key-distribution.jar" ]
