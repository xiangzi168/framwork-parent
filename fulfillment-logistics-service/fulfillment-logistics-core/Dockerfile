FROM java:8
ARG JAR_FILE
ADD target/${JAR_FILE} /app.jar
EXPOSE 8085
ADD register.sh /
RUN chmod +x /register.sh
ENTRYPOINT ["sh","-c","/register.sh"]
MAINTAINER Mirror 323168973@qq.com
