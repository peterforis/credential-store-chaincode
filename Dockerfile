FROM gradle:jdk11-alpine AS GRADLE_BUILD

COPY src/ src/
COPY build.gradle ./ 

RUN gradle --no-daemon build shadowJar -x checkstyleMain -x checkstyleTest

FROM openjdk:11-jre
ARG CC_SERVER_PORT=9999

ENV TINI_VERSION v0.19.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

RUN addgroup --system javauser && useradd -g javauser javauser

# copy only the artifacts we need from the first stage and discard the rest
COPY --chown=javauser:javauser --from=GRADLE_BUILD /home/gradle/build/libs/chaincode.jar /chaincode.jar
COPY --chown=javauser:javauser docker/docker-entrypoint.sh /docker-entrypoint.sh 

ENV PORT $CC_SERVER_PORT
EXPOSE $CC_SERVER_PORT

USER javauser
ENTRYPOINT [ "/tini", "--", "/docker-entrypoint.sh" ]
