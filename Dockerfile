FROM amazoncorretto:21 as builder

WORKDIR /app

RUN yum update -y && yum install -y tar wget gzip && yum clean all


COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests

# --- Etapa Final (Runtime) ---
FROM amazoncorretto:21


RUN yum update -y && \
    yum install -y wget unzip libX11 maven && \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum install -y ./google-chrome-stable_current_x86_64.rpm && \
    rm google-chrome-stable_current_x86_64.rpm && \
    wget https://storage.googleapis.com/chrome-for-testing-public/135.0.7049.84/linux64/chromedriver-linux64.zip && \
    unzip chromedriver-linux64.zip && \
    mv chromedriver-linux64/chromedriver /usr/bin/chromedriver && \
    chmod +x /usr/bin/chromedriver


ENV CHROME_BIN="/usr/bin/google-chrome"
ENV PATH="/usr/bin:${PATH}"

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java -jar app.jar"]

