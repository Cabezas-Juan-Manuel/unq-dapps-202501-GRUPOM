# --- Etapa de Construcción (Builder) ---
# Usamos una imagen base con JDK 21 y Maven para compilar la aplicación
FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw package -DskipTests

# --- Etapa Final (Runtime) ---
# Usamos una imagen base con JRE 21
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

USER root
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    grep \
    jq \
    fonts-liberation \
    libu2f-udev \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

RUN CHROME_DRIVER_VERSION=$(wget -qO- https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions.json | jq -r '.channels.Stable.version') && \
    echo "Detected ChromeDriver Version: $CHROME_DRIVER_VERSION" && \
    # Añade una comprobación para asegurarte de que la versión no está vacía
    if [ -z "$CHROME_DRIVER_VERSION" ]; then echo "Error: ChromeDriver version not found."; exit 1; fi && \
    # Guarda la versión en un archivo temporal para usarla en el siguiente paso
    echo $CHROME_DRIVER_VERSION > /tmp/chrome_driver_version.txt

RUN CHROME_DRIVER_VERSION=$(cat /tmp/chrome_driver_version.txt) && \
    echo "Downloading ChromeDriver Version: $CHROME_DRIVER_VERSION" && \
    wget -q --continue -P /tmp "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_DRIVER_VERSION}/linux64/chromedriver-linux64.zip"


RUN unzip /tmp/chromedriver-linux64.zip -d /tmp && \
    mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    chown root:root /usr/local/bin/chromedriver && \
    chmod +x /usr/local/bin/chromedriver && \
    # Limpieza
    rm -rf /tmp/chromedriver* /tmp/chrome_driver_version.txt

#
WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
