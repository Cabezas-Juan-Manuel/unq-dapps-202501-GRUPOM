package ar.edu.unq.pronostico.deportivo.service.integration;

import ar.edu.unq.pronostico.deportivo.model.Player;
import ar.edu.unq.pronostico.deportivo.utils.AppLogger;
import ar.edu.unq.pronostico.deportivo.utils.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

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

    private WebDriver configureWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    private Map<String, String> generateMapNameId() {
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("team-stats", "top-team-stats-summary-grid");
        dictionary.put("team-players", "top-player-stats-summary-grid");
        dictionary.put("player-stats", "top-player-stats-summary-grid");
        dictionary.put("player-latest-matches", "player-matches-table");
        return dictionary;
    }

    private Element getTableByTitle(Document doc, String title) {
        Elements h2s = doc.select("h2");
        for (Element h2 : h2s) {
            if (h2.text().toLowerCase().contains(title.toLowerCase())) {
                return h2.nextElementSibling();
            }
        }
        return null;
    }

    private Element getTableById(Document doc, String id) {
        return doc.selectFirst("[id='" + id + "']");
    }

    private List<Map<String, String>> getTableContent(Element table) {
        Elements tableChildren = table.children();
        List<Map<String, String>> data = new ArrayList<>();

        if (tableChildren.size() != 2) {
            return manageTableWithOnlyBody(tableChildren);
        }

        Element header = tableChildren.get(0);
        Element body = tableChildren.get(1);

        Elements headerElements = Objects.requireNonNull(header.firstElementChild()).children();
        List<String> headers = new ArrayList<>();
        for (Element th : headerElements) {
            headers.add(th.text().trim());
        }

        Elements bodyElements = body.children();
        for (Element row : bodyElements) {
            List<String> values = getValuesFromRow(row);
            Map<String, String> rowDict = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                rowDict.put(headers.get(i), values.get(i));
            }
            data.add(rowDict);
        }
        return data;
    }

    private List<Map<String, String>> manageTableWithOnlyBody(Elements tableRows) {
        List<Map<String, String>> data = new ArrayList<>();

        for (Element row : tableRows) {
            Elements cells = row.select("td");
            Map<String, String> rowMap = new HashMap<>();

            for (Element cell : cells) {
                String key = cell.className().trim();  // Puede ser vacío si no tiene class
                String value = cell.text().trim();

                // Evitar sobrescribir si hay claves duplicadas vacías
                if (key.isEmpty()) {
                    key = "column_" + rowMap.size();  // Asignar una clave temporal
                }

                rowMap.put(key, value);
            }

            data.add(rowMap);
        }

        return data;
    }


    private List<String> getValuesFromRow(Element row) {
        List<String> extractedTexts = new ArrayList<>();
        Elements items = row.children();
        for (Element item : items) {
            String text = item.text().trim();
            if (!text.isEmpty()) {
                Element aChild = item.selectFirst("a");
                if (aChild != null) {
                    extractedTexts.add(aChild.text().trim());
                } else if (text.matches("\\d+\\(\\d+\\)")) {
                    String[] parts = text.split("[() ]+");
                    int total = Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
                    extractedTexts.add(String.valueOf(total));
                } else {
                    extractedTexts.add(text);
                }
            }
        }
        return extractedTexts;
    }

    private String getDataFromTableOnWeb(String text, String searchBy, String tableBy) {
        String serviceMethod = "getDataFromTableOnWeb";
        String serviceClass = WhoScoredService.class.getName();

        if (!searchBy.equals("player") && !searchBy.equals("team")) {
            AppLogger.error(serviceClass, serviceMethod, "Invalid value for searchBy. Must be 'player' or 'team'");
            throw new IllegalArgumentException("Available arguments: player, team");
        }
        if (!tableBy.equals("team-stats") && !tableBy.equals("team-players") &&
                !tableBy.equals("player-stats") && !tableBy.equals("player-latest-matches")) {
            AppLogger.error(serviceClass, serviceMethod, "Invalid value for tableBy");
            throw new IllegalArgumentException("Available arguments: team-stats, team-players");
        }

        String jsonOutput = "";

        Map<String, String> dicIds = generateMapNameId();

        WebDriver driver = configureWebDriver();

        try {
            String baseURL = "https://whoscored.com";
            driver.get(baseURL + "/search/?t=" + text);

            String pageSource = driver.getPageSource();

            if (pageSource == null) {
                AppLogger.error(serviceClass, serviceMethod, "Page source is empty");
                return jsonOutput;
            }

            Document soup = Jsoup.parse(pageSource);

            Element table = getTableByTitle(soup, searchBy);

            if (table == null) {
                AppLogger.error(serviceClass, serviceMethod, "Table by title not found");
                return jsonOutput;
            }

            Element linkElement = table.selectFirst("a[href]");
            if (linkElement == null) {
                AppLogger.error(serviceClass, serviceMethod, "No link found in results table");
                return jsonOutput;
            }
            String relativeURL = linkElement.attr("href");
            String fullURL = baseURL + relativeURL;
            driver.get(fullURL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(dicIds.get(tableBy))));

            pageSource = driver.getPageSource();
            if (pageSource == null) {
                AppLogger.error(serviceClass, serviceMethod, "Page source is empty for search term");
                return jsonOutput;
            }

            soup = Jsoup.parse(pageSource);

            Element dataTable = getTableById(soup, dicIds.get(tableBy));
            if (dataTable == null) {
                AppLogger.error(serviceClass, serviceMethod, "Table by id not found");
                return jsonOutput;
            }

            List<Map<String, String>> data = getTableContent(dataTable);
            if (data.isEmpty()) {
                AppLogger.error(serviceMethod, serviceClass, "Table by title not found");
            }
            ObjectMapper mapper = new ObjectMapper();
            jsonOutput = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            AppLogger.error(serviceClass, serviceMethod, "Object mapper error");
            throw new IllegalStateException(e);
        } finally {
            driver.quit();
        }
        return jsonOutput;
    }

    public List<Player> getPlayersFromTeam(String teamName) {
        String jsonString = getDataFromTableOnWeb(teamName, "team", "team-players");
        if (jsonString == null) {
            return new ArrayList<>();
        }
        return JsonParser.fromJsonToPlayerList(jsonString);
    }


    public Player getPlayerStatics(String playerName) {
        String jsonString = getDataFromPlayerOnWeb(playerName);
        return JsonParser.fromJsonToPlayer(jsonString);
    }


    private String getDataFromPlayerOnWeb(String playerName){
        String playerStatsTableId = "top-player-stats-summary-grid";
        String defensiveTableId = "player-tournament-stats-defensive";
        String offensiveTableId = "player-tournament-stats-offensive";
        String divPath = "div.col12-lg-6.col12-m-6.col12-s-6.col12-xs-12";
        String cssToOffensiveStatsPage = "#player-tournament-stats-options > li:nth-child(3) > a";
        String cssToDefensiveStatsPage = "#player-tournament-stats-options li:nth-child(2) a";
        WebDriver driver = configureWebDriver();

        Document playersPage = goToPlayersPage(playerName, driver);

        Elements playerInfoTable = playersPage.select(divPath);
        Element defensiveStatsTable = getDinamicTable(driver, cssToDefensiveStatsPage, defensiveTableId);
        System.out.println("selenium la concha de tu madre");
        Element offensiveStatsTable = getDinamicTable(driver, cssToOffensiveStatsPage, offensiveTableId);
        String jsonPlayerData =  transformTablesToJson(defensiveStatsTable, offensiveStatsTable, playerInfoTable);
        return "new Player()";
    }

    private Element getDinamicTable(WebDriver driver, String cssPathToTab, String tableWrapperId) {
        driver.findElement(By.cssSelector(cssPathToTab)).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Mantén o ajusta este timeout

        // Espera a que el div contenedor esté presente
        WebElement tableWrapperDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tableWrapperId)));

        // Espera a que el elemento <table> anidado esté presente y obtén una referencia a l
        WebElement nestedTableWebElement;
        try {
            nestedTableWebElement = wait.until(
                    ExpectedConditions.presenceOfNestedElementLocatedBy(tableWrapperDiv, By.tagName("table"))
            );
        } catch (Exception e) {
            AppLogger.warn(WhoScoredService.class.getName(), "getDinamicTable", "Timeout esperando la tabla anidada en: " + tableWrapperId + ". Error: " + e.getMessage());
            // System.out.println("DEBUG: No se encontró la tabla anidada en " + tableWrapperId);
            return null; // Retorna null si la tabla no se encuentra
        }

        // Una vez que Selenium confirma que el WebElement de la tabla existe, obtén su outerHTML.
        // outerHTML incluye el propio tag <table> y su contenido.
        String tableHtml = nestedTableWebElement.getAttribute("outerHTML");

        if (tableHtml == null || tableHtml.isEmpty()) {
            AppLogger.warn(WhoScoredService.class.getName(), "getDinamicTable", "outerHTML de la tabla anidada está vacío para: " + tableWrapperId);
            // System.out.println("DEBUG: outerHTML de la tabla anidada estaba vacío en " + tableWrapperId);
            return null;
        }

        Document doc = Jsoup.parse(tableHtml);
        // Como parseamos el outerHTML de la tabla, el elemento <table> debería ser el primer hijo del body
        // o podemos seleccionarlo directamente. selectFirst("table") es más seguro.
        Element tableElement = doc.selectFirst("table");

        // if (tableElement == null) {
        //     System.out.println("DEBUG: Jsoup no pudo parsear la tabla desde su outerHTML para " + tableWrapperId);
        //     System.out.println("DEBUG: HTML de la tabla era: " + tableHtml.substring(0, Math.min(tableHtml.length(), 300)));
        // }
        return tableElement;
    }

    private String transformTablesToJson(Element playerDefensiveStatsTable, Element playerOffensiveStatsTable, Elements playerInfoTable){
        String playerInfo = getContentFromDiv(playerInfoTable);
        List<Map<String, String>> defensiveStats = getTableContent(playerDefensiveStatsTable);
        List<Map<String, String>> offensiveStats = getTableContent(playerOffensiveStatsTable);
        String playerDataForPlayersCreation = "";
        return playerDataForPlayersCreation;
    }

    private String getContentFromDiv(Elements divPath){
        Map<String, String> playersInfo = new HashMap<>();

        for (Element row : divPath) {
            Element labelSpan = row.getElementsByTag("span").first();
            if (labelSpan != null) {
                String key = labelSpan.text().trim();
                String value = row.text().replace(labelSpan.text(), "").trim();
                playersInfo.put(key, value);
                }
            }
        ObjectMapper mapper = new ObjectMapper();

        try{
            return mapper.writeValueAsString(playersInfo);
        } catch (JsonProcessingException e){
            throw  new RuntimeException(e.getMessage());
        }
    }



    private Document goToPlayersPage(String player, WebDriver driver) {
        String baseURL = "https://whoscored.com";
        driver.get(baseURL + "/search/?t=" + player);
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        Element table = getTableByTitle(document, "Players:");
        Element linkElement = table.selectFirst("a[href]");
        String relativeURL = linkElement.attr("href");
        String fullURL = baseURL + relativeURL;
        driver.get(fullURL);
        return Jsoup.parse(driver.getPageSource());
    }

    private static void addPageToList(WebDriver driver, String cssSelector, List<Document> list) {
        if (cssSelector != null){
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
            element.click();
        }
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        String hash = Integer.toHexString(pageSource.hashCode());
        System.out.println("Page hash: " + hash);
        list.add(document);
    }
}
