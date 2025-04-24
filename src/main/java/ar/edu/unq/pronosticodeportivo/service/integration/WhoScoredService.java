package ar.edu.unq.pronosticodeportivo.service.integration; // Asegúrate que el paquete sea el correcto para tu proyecto Java

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WhoScoredService {

    /**
     * Configura y crea una instancia de WebDriver para Chrome.
     *
     * @return Una instancia de WebDriver configurada.
     */
    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
        );
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        // Equivalente a: setBinary(System.getenv("CHROME_BIN") ?: "/usr/bin/google-chrome")
        // Si necesitas especificar el binario, descomenta la siguiente línea y ajusta la ruta si es necesario.
        // String chromeBinaryPath = Optional.ofNullable(System.getenv("CHROME_BIN")).orElse("/usr/bin/google-chrome");
        // options.setBinary(chromeBinaryPath);

        return new ChromeDriver(options);
    }


    public static List<String> fetchPlayers(String teamName) {
        WebDriver driver = createDriver();
        String baseUrl = "https://es.whoscored.com";
        List<String> teamPlayers; // Declarar fuera del try para que esté en el scope del finally

        try {
            // Navegar a la página principal de la liga
            driver.get(baseUrl + "/regions/11/tournaments/68/seasons/10573/argentina-liga-profesional");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120)); // Aumentar timeout si es necesario

            // Esperar y encontrar la tabla de posiciones
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("standings")));
            WebElement teamsTable = driver.findElement(By.className("standings"));

            // Esperar y obtener los enlaces de los equipos
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-link")));
            List<WebElement> links = teamsTable.findElements(By.className("team-link"));

            // Encontrar el enlace del equipo deseado (ignorando mayúsculas/minúsculas y buscando coincidencias parciales)
            String targetLink = links.stream()
                    .filter(link -> link.getText().equalsIgnoreCase(teamName) || link.getText().toLowerCase().contains(teamName.toLowerCase()))
                    .findFirst()
                    .map(link -> link.getAttribute("href"))
                    .orElseThrow(() -> new RuntimeException(teamName + " not found")); // team not found

            // Navegar a la página del equipo
            driver.get(targetLink);

            // Esperar y encontrar la tabla de estadísticas de jugadores
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")));
            WebElement playersTable = driver.findElement(By.id("player-table-statistics-body"));

            // Esperar a que los elementos de los jugadores estén presentes
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("iconize")));

            // Extraer los nombres de los jugadores, filtrando los vacíos
            teamPlayers = playersTable.findElements(By.className("iconize"))
                    .stream()
                    .map(WebElement::getText) // Obtener el texto de cada elemento
                    .filter(text -> text != null && !text.isBlank()) // Filtrar nulos y vacíos/blancos
                    .collect(Collectors.toList()); // Recolectar en una lista

            // Verificar si se encontraron jugadores
            if (teamPlayers.isEmpty()) {
                throw new RuntimeException("Players of " + teamName + " not found");
            }

        } finally {
            // Asegurarse de cerrar el driver incluso si ocurre una excepción
            if (driver != null) {
                driver.quit();
            }
        }

        return teamPlayers;
    }
}
