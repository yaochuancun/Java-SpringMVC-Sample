FROM tomcat:8.5
VOLUME /usr/local/tomcat

ENV TIME_ZONE              Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE test

RUN echo "$TIME_ZONE" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

ADD ./TaskMananger.war /usr/local/tomcat/webapps/
ENTRYPOINT ./bin/startup.sh && bash
