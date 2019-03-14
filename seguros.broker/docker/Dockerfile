FROM frolvlad/alpine-oraclejdk8-temporary:slim
MAINTAINER jnempu@gmail.cl
ENV APP_DIR /mps
ENV APP_FILE ${APP_DIR}/application.jar
ENV APP_CONFIG_FILE ${APP_DIR}/application.properties
RUN mkdir -p ${APP_DIR}
ADD seguros.broker-0.0.1.jar ${APP_FILE}
ADD applicationTemplate.properties ${APP_CONFIG_FILE}
RUN sh -c 'chown root -R $APP_DIR'
VOLUME $APP_DIR
ENV JAVA_OPTS="-server  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms1024m -Xmx1536m "
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar $APP_FILE --spring.config.location=file:$APP_CONFIG_FILE" ]
EXPOSE 8080