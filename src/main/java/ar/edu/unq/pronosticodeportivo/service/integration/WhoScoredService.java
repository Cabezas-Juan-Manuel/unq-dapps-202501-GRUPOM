package ar.edu.unq.pronosticodeportivo.service.integration;

import ar.edu.unq.pronosticodeportivo.model.Player;
// Importa SLF4J si lo vas a usar para logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.unq.pronosticodeportivo.utils.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy; // Asegúrate de importar Proxy

import java.util.*;

import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class WhoScoredService {

    // Usa SLF4J Logger (recomendado)
    private static final Logger log = LoggerFactory.getLogger(WhoScoredService.class);

    // Constructor privado no es necesario si es un @Service y los métodos son estáticos
    // Considera hacer los métodos no estáticos si usas inyección de dependencias
    // private WhoScoredService() {}

    private static WebDriver configureWebDriver() {
        log.info("Configuring WebDriver...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // --- Configuración del Proxy (Hardcodeado) ---
        // !! REEMPLAZA ESTOS VALORES CON UN PROXY REAL DE UNA LISTA GRATUITA !!
        String proxyHost = "45.130.253.71"; // <-- EJEMPLO: Reemplaza con la IP del proxy gratuito
        String proxyPort = "8000";          // <-- EJEMPLO: Reemplaza con el puerto del proxy gratuito
        // --------------------------------------------------------------------

        // String proxyUser = System.getenv("PROXY_USER"); // Comentado o eliminado
        // String proxyPass = System.getenv("PROXY_PASSWORD"); // Comentado o eliminado

        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null && !proxyPort.isEmpty()) {
            log.info("Hardcoded proxy configuration found. Host: {}, Port: {}", proxyHost, proxyPort);
            Proxy proxy = new Proxy();
            String proxyAddress = proxyHost + ":" + proxyPort;

            proxy.setHttpProxy(proxyAddress);
            proxy.setSslProxy(proxyAddress); // Configura para HTTP y HTTPS
            // proxy.setProxyType(Proxy.ProxyType.MANUAL); // Asegúrate que sea manual

            options.setProxy(proxy);
            log.info("WebDriver configured to use hardcoded proxy: {}", proxyAddress);
        } else {
            log.warn("Hardcoded proxy host or port is empty. Proceeding without proxy.");
        }
        // -----------------------------

        log.info("WebDriver configuration complete.");
        return new ChromeDriver(options);
    }

    // --- RESTO DE LOS MÉTODOS (generateMapNameId, getTableByTitle, etc.) ---
    // ... (sin cambios, pero considera usar SLF4J para logging en lugar de System.out)
    private static Map<String, String> generateMapNameId() {
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("team-stats", "top-team-stats-summary-grid");
        dictionary.put("team-players", "top-player-stats-summary-grid");
        dictionary.put("player-stats", "top-player-stats-summary-grid");
        dictionary.put("player-latest-matches", "player-matches-table");
        return dictionary;
    }

    private static Element getTableByTitle(Document doc, String title) {
        // Reemplaza System.out con logging
        // log.debug("Searching for table with title containing: {}", title);
        // log.trace("Document source for title search:\n{}", doc.html()); // Muy verboso
        Elements h2s = doc.select("h2");
        for (Element h2 : h2s) {
            if (h2.text().toLowerCase().contains(title.toLowerCase())) {
                // log.debug("Found matching h2: {}", h2.text());
                return h2.nextElementSibling();
            }
        }
        log.warn("Table with title containing '{}' not found.", title);
        return null;
    }

    private static Element getTableById(Document doc, String id) {
        Element table = doc.selectFirst("#" + id); // Selector CSS es más estándar
        if (table == null) {
            log.warn("Table with id '{}' not found.", id);
        }
        return table;
    }

    // Considera refactorizar getTableContent para mayor robustez
    private static List<Map<String, String>> getTableContent(Element table) {
        List<Map<String, String>> data = new ArrayList<>();
        Element thead = table.selectFirst("thead");
        Element tbody = table.selectFirst("tbody");

        if (thead == null || tbody == null) {
            log.warn("Table structure unexpected. Missing thead or tbody. Table HTML:\n{}", table.outerHtml());
            return data; // Devuelve lista vacía si la estructura no es la esperada
        }

        Element headerRow = thead.selectFirst("tr");
        if (headerRow == null) {
            log.warn("Table header row (tr) not found in thead.");
            return data;
        }
        List<String> headers = new ArrayList<>();
        for (Element th : headerRow.select("th")) { // Seleccionar 'th'
            headers.add(th.text().trim());
        }

        Elements bodyRows = tbody.select("tr"); // Seleccionar 'tr' en tbody
        for (Element row : bodyRows) {
            List<String> values = getValuesFromRow(row);
            // Asegurarse que el ncida con el de cabeceras
            if (headers.size() == values.size()) {
                Map<String, String> rowDict = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    rowDict.put(headers.get(i), values.get(i));
                }
                data.add(rowDict);
            } else {
                log.warn("Header count ({}) and value count ({}) mismatch for row: {}", headers.size(), values.size(), row.outerHtml());
            }
        }
        return data;
    }

    // Helper para extraer valores de celdas 'td'
    private static List<String> getValuesFromRow(Element row) {
        List<String> extractedTexts = new ArrayList<>();
        Elements items = row.select("td"); // Seleccionar 'td'
        for (Element item : items) {
            String text = item.text().trim();
            // Tu lógica existente para manejar 'a' y números con paréntesis
            Element aChild = item.selectFirst("a");
            if (aChild != null) {
                extractedTexts.add(aChild.text().trim());
            } else if (text.matches("\\d+\\(\\d+\\)")) {
                try {
                    String[] parts = text.split("[() ]+");
                    // Validar que parts tenga al menos 2 elementos antes de accederlos
                    if (parts.length >= 2) {
                        int total = Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
                        extractedTexts.add(String.valueOf(total));
                    } else {
                        extractedTexts.add(text); // Fallback si el formato no es el esperado
                        log.warn("Unexpected format for numeric split: {}", text);
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { // Capturar posibles errores
                    extractedTexts.add(text); // Fallback en caso de error
                    log.warn("Could not parse numeric value: {}", text, e);
                }
            } else {
                extractedTexts.add(text);
            }
        }
        return extractedTexts;
    }


    // Método principal de scraping
    private static String getDataFromTableOnWeb(String text, String searchBy, String tableBy) {
        // Validación de entrada (usando SLF4J)
        if (!Set.of("player", "team").contains(searchBy)) {
            log.error("Invalid value for searchBy: {}. Must be 'player' or 'team'", searchBy);
            throw new IllegalArgumentException("Available arguments for searchBy: player, team");
        }
        Set<String> validTableBy = Set.of("team-stats", "team-players", "player-stats", "player-latest-matches");
        if (!validTableBy.contains(tableBy)) {
            log.error("Invalid value for tableBy: {}", tableBy);
            throw new IllegalArgumentException("Available arguments for tableBy: " + validTableBy);
        }

        Map<String, String> dicIds = generateMapNameId();
        WebDriver driver = null; // Inicializar a null
        String jsonOutput = "";
        ObjectMapper mapper = new ObjectMapper(); // Reutilizar ObjectMapper

        try {
            driver = configureWebDriver(); // Llama al método modificado con proxy hardcodeado
            String baseURL = "https://whoscored.com";
            String searchURL = baseURL + "/search/?t=" + text;
            log.info("Navigating to search URL: {}", searchURL);
            driver.get(searchURL);

            // *** Chequeo inmediato de Cloudflare ***
            if (driver.getTitle().contains("Cloudflare") || driver.getPageSource().contains("cf-wrapper")) {
                log.error("Blocked by Cloudflare immediately after accessing search URL: {}", searchURL);
                // Considera guardar el HTML de bloqueo para depurar si es necesario
                // log.debug("Cloudflare block page HTML:\n{}", driver.getPageSource());
                throw new IllegalStateException("Blocked by Cloudflare security check. Scraping from this environment is detected.");
            }

            // Esperar a que aparezca el título de la tabla de resultados
            WebDriverWait initialWait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Ajusta el tiempo si es necesario
            // Selector XPath más robusto (insensible a mayúsculas/minúsculas)
            By titleSelector = By.xpath("//h2[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchBy.toLowerCase() + "')]");
            try {
                initialWait.until(ExpectedConditions.visibilityOfElementLocated(titleSelector));
                log.info("Search results page loaded successfully.");
            } catch (org.openqa.selenium.TimeoutException e) {
                log.error("Timeout waiting for search results title '{}' on page: {}", searchBy, searchURL);
                // Loguear inicio del source para ver qué se cargó
                // log.debug("Page source on timeout:\n{}", driver.getPageSource().substring(0, Math.min(driver.getPageSource().length(), 1000)));
                if (driver.getTitle().contains("Cloudflare") || driver.getPageSource().contains("cf-wrapper")) {
                    throw new IllegalStateException("Blocked by Cloudflare during search results load.");
                }
                // Lanzar excepción en lugar de devolver ""
                throw new IllegalStateException("Timeout waiting for search results, page might not have loaded correctly or structure changed.", e);
            }

            String pageSource = driver.getPageSource();
            Document soup = Jsoup.parse(pageSource);

            Element resultsTable = getTableByTitle(soup, searchBy);
            if (resultsTable == null) {
                log.error("Results table (identified by title '{}') not found on search page: {}", searchBy, searchURL);
                if (driver.getTitle().contains("Cloudflare") || driver.getPageSource().contains("cf-wrapper")) {
                    throw new IllegalStateException("Blocked by Cloudflare; results table not found.");
                }
                // Lanzar excepción
                throw new IllegalStateException("Could not find the results table on the search page.");
            }

            Element linkElement = resultsTable.selectFirst("a[href]");
            if (linkElement == null) {
                log.error("No link found within the results table on search page: {}", searchURL);
                // log.debug("Results table HTML:\n{}", resultsTable.outerHtml());
                // Lanzar excepción
                throw new IllegalStateException("Could not find the navigation link in the results table.");
            }

            String relativeURL = linkElement.attr("href");
            String fullURL = baseURL + relativeURL;
            log.info("Navigating to entity page: {}", fullURL);
            driver.get(fullURL);

            // *** Chequeo de Cloudflare en la página de entidad ***
            if (driver.getTitle().contains("Cloudflare") || driver.getPageSource().contains("cf-wrapper")) {
                log.error("Blocked by Cloudflare after accessing entity URL: {}", fullURL);
                throw new IllegalStateException("Blocked by Cloudflare security check on entity page.");
            }

            // Esperar a que la tabla de datos específica sea visible
            String targetTableId = dicIds.get(tableBy);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Ajusta el tiempo
            By tableLocator = By.id(targetTableId);
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(tableLocator));
                log.info("Target data table with id '{}' is visible.", targetTableId);
            } catch (org.openqa.selenium.TimeoutException e) {
                log.error("Timeout waiting for data table with id '{}' on page: {}", targetTableId, fullURL);
                // log.debug("Page source on data table timeout:\n{}", driver.getPageSource().substring(0, Math.min(driver.getPageSource().length(), 1000)));
                if (driver.getTitle().contains("Cloudflare") || driver.getPageSource().contains("cf-wrapper")) {
                    throw new IllegalStateException("Blocked by Cloudflare during data table load.");
                }
                // Lanzar excepción
                throw new IllegalStateException("Timeout waiting for the target data table (id: " + targetTableId + ").", e);
            }

            pageSource = driver.getPageSource();
            soup = Jsoup.parse(pageSource);

            Element dataTable = getTableById(soup, targetTableId);
            if (dataTable == null) {
                // Logging ya se hace en getTableById
                // Lanzar excepción
                throw new IllegalStateException("Could not find the target data table (id: " + targetTableId + ") even after waiting.");
            }

            List<Map<String, String>> data = getTableContent(dataTable);
            if (data.isEmpty()) {
                log.warn("Extracted data table content is empty for table id '{}' on page: {}", targetTableId, fullURL);
            } else {
                log.info("Successfully extracted {} rows from table id '{}'", data.size(), targetTableId);
            }

            jsonOutput = mapper.writeValueAsString(data);

        } catch (JsonProcessingException e) {
            log.error("Error converting extracted data to JSON for search term '{}'", text, e);
            // Lanzar una excepción más específica o envolverla
            throw new RuntimeException("Failed to process scraped data into JSON", e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Re-lanzar excepciones de validación, bloqueo o estado inesperado
            throw e;
        } catch (Exception e) {
            // Capturar excepciones más amplias (WebDriverException, etc.)
            log.error("An unexpected error occurred during scraping for text '{}'", text, e);
            // Lanzar una excepción genérica o envolver la original
            throw new RuntimeException("Unexpected scraping error for: " + text, e);
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                    log.info("WebDriver quit successfully for search term '{}'.", text);
                } catch (Exception e) {
                    log.error("Error quitting WebDriver for search term '{}'", text, e);
                }
            }
        }
        return jsonOutput;
    }

    // Método público
    public static List<Player> getPlayersFromTeam(String teamName) {
        try {
            String jsonString = getDataFromTableOnWeb(teamName, "team", "team-players");
            // No es necesario chequear null/empty si getDataFromTableOnWeb lanza excepciones
            return JsonParser.fromJsonToPlayerList(jsonString);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Loguear y re-lanzar excepciones específicas
            log.error("Failed to get players for team '{}' due to: {}", teamName, e.getMessage());
            throw e; // Permite que el Controller maneje esto
        } catch (Exception e) {
            // Capturar otros errores
            log.error("Unexpected error getting players for team '{}'", teamName, e);
            // Lanzar una excepción para que el Controller sepa que algo falló
            throw new RuntimeException("Failed to retrieve players for team: " + teamName, e);
        }
    }
}
