FROM public.ecr.aws/docker/library/openjdk:16-slim-buster

ARG JAR_FILE
ADD $JAR_FILE /opt/phaedra/app.jar

ENV USER phaedra
RUN useradd -c 'phaedra user' -m -d /home/$USER -s /bin/nologin $USER
WORKDIR /opt/phaedra
USER $USER

CMD ["java", "-jar", "/opt/phaedra/app.jar", "--spring.jmx.enabled=false"]
