# 使用官方 Java 运行环境
FROM eclipse-temurin:21-jre-jammy

RUN apt-get update \
    && apt-get install -y docker.io \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 拷贝 jar 包
COPY target/*.jar app.jar

# 暴露端口（根据你的项目端口）
EXPOSE 8080

ENV JAVA_OPTS="-Duser.timezone=Asia/Shanghai"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# docker build -t misaki-api .