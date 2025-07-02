package ar.edu.unq.pronostico.deportivo.service.integration;

import ar.edu.unq.pronostico.deportivo.model.PlayerForTeam;
import ar.edu.unq.pronostico.deportivo.utils.AppLogger;
import ar.edu.unq.pronostico.deportivo.utils.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    String teamPlayersIdentifier = "team-players";
    String teamStatsIdentifier = "team-stats";
    String playerStatsIdentifier = "player-stats";
    String playerLatestMatchesIdentifier = "player-latest-matches";
    String baseURL = "https://whoscored.com";
    String searchBaseURL = "https://whoscored.com/search/?t=";
    String firstLink = "a[href]";


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
        dictionary.put(teamStatsIdentifier, "top-team-stats-summary-grid");
        dictionary.put(teamPlayersIdentifier, "top-player-stats-summary-grid");
        dictionary.put(playerStatsIdentifier, "top-player-stats-summary-grid");
        dictionary.put(playerLatestMatchesIdentifier, "player-matches-table");
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
        if (!tableBy.equals(teamStatsIdentifier) && !tableBy.equals(teamPlayersIdentifier) &&
                !tableBy.equals(playerStatsIdentifier) && !tableBy.equals(playerLatestMatchesIdentifier)) {
            AppLogger.error(serviceClass, serviceMethod, "Invalid value for tableBy");
            throw new IllegalArgumentException("Available arguments: teamStatsIdentifier, team-players");
        }

        String jsonOutput = "";

        Map<String, String> dicIds = generateMapNameId();

        WebDriver driver = configureWebDriver();

        try {
            driver.get(searchBaseURL + text);

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

            Element linkElement = table.selectFirst(firstLink);
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

    public List<PlayerForTeam> getPlayersFromTeam(String teamName) {
        String jsonString = getDataFromTableOnWeb(teamName, "team", teamPlayersIdentifier);
        if (jsonString == null) {
            return new ArrayList<>();
        }
        return JsonParser.fromJsonToPlayerList(jsonString);
    }


    public List<Map<String, String>> getPlayerStatics(String playerName) {
        return getDataFromPlayerOnWeb(playerName);
    }


    private List<Map<String, String>> getDataFromPlayerOnWeb(String playerName){
        String defensiveTableId = "player-tournament-stats-defensive";
        String offensiveTableId = "player-tournament-stats-offensive";
        String divPath = "div.col12-lg-6.col12-m-6.col12-s-6.col12-xs-12";
        String cssToOffensiveStatsPage = "#player-tournament-stats-options > li:nth-child(3) > a";
        String cssToDefensiveStatsPage = "#player-tournament-stats-options li:nth-child(2) a";
        WebDriver driver = configureWebDriver();

        Document playersPage = goToPlayersPage(playerName, driver);

        List<Map<String, String>> listOfTables = new ArrayList<>();
        Elements playerInfoTable = playersPage.select(divPath);
        Map<String, String> playerInfo = getContentFromDiv(playerInfoTable);
        listOfTables.add(playerInfo);

        Element defensiveStatsTable = getDinamicTable(driver, cssToDefensiveStatsPage, defensiveTableId);
        Element offensiveStatsTable = getDinamicTable(driver, cssToOffensiveStatsPage, offensiveTableId);

        return transformTablesToMap(defensiveStatsTable, offensiveStatsTable, listOfTables);
    }

    private Element getDinamicTable(WebDriver driver, String cssPathToTab, String tableWrapperId) {
        driver.findElement(By.cssSelector(cssPathToTab)).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));


        WebElement tableWrapperDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tableWrapperId)));


        WebElement nestedTableWebElement;
        try {
            nestedTableWebElement = wait.until(
                    ExpectedConditions.presenceOfNestedElementLocatedBy(tableWrapperDiv, By.tagName("table"))
            );
        } catch (Exception e) {
            AppLogger.warn(WhoScoredService.class.getName(), "getDinamicTable", "Timeout esperando la tabla anidada en: " + tableWrapperId + ". Error: " + e.getMessage());

            return null;
        }

        String tableHtml = nestedTableWebElement.getAttribute("outerHTML");

        if (tableHtml == null || tableHtml.isEmpty()) {
            AppLogger.warn(WhoScoredService.class.getName(), "getDinamicTable", "outerHTML de la tabla anidada está vacío para: " + tableWrapperId);
            return null;
        }

        Document doc = Jsoup.parse(tableHtml);
        return doc.selectFirst("table");
    }


    public  List<Map<String, String>> transformTablesToMap(Element playerDefensiveStatsTable, Element playerOffensiveStatsTable, List<Map<String, String>> listOfTables) {

        List<Map<String, String>> allDefensiveStats = getTableContent(playerDefensiveStatsTable);
        List<Map<String, String>> allOffensiveStats = getTableContent(playerOffensiveStatsTable);

        Map<String, String> averageDefensiveStats = allDefensiveStats.getLast();
        Map <String, String> averageOffensiveStats = allOffensiveStats.getLast();

        listOfTables.add(averageOffensiveStats);
        listOfTables.add(averageDefensiveStats);

        return listOfTables;

    }

    private Map<String, String> getContentFromDiv(Elements divPath) {
        Map<String, String> playersInfo = new HashMap<>();

        for (Element row : divPath) {
            Element labelSpan = row.getElementsByTag("span").first();
            if (labelSpan != null) {
                String key = labelSpan.text().trim();
                String value = row.text().replace(labelSpan.text(), "").trim();
                playersInfo.put(key, value);
            }
        }

        return playersInfo;
    }

    private Document goToPlayersPage(String player, WebDriver driver) {
        driver.get(searchBaseURL + player);
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        Element table = getTableByTitle(document, "Players:");
        Element linkElement = table.selectFirst(firstLink);
        String relativeURL = linkElement.attr("href");
        String fullURL = baseURL + relativeURL;
        driver.get(fullURL);
        return Jsoup.parse(driver.getPageSource());
    }

    public List<Map<String, String>> getStatisticsForTeam(String team) {
        WebDriver driver = configureWebDriver();
        String topTeamStatsDefensive = "top-team-stats-defensive";
        String topTeamStatsOffensive = "top-team-stats-offensive";
        String cssToDefensivePath = "#top-team-stats-options > li:nth-child(2) > a:nth-child(1)";
        String cssToOffensivePath = "#top-team-stats-options > li:nth-child(3) > a:nth-child(1)";

        goToPageOfSearchedItem(team, driver, "Teams:");;

        Element defensiveStatsTable = getDinamicTable(driver, cssToDefensivePath, topTeamStatsDefensive);
        Element offensiveStatsTable = getDinamicTable(driver, cssToOffensivePath, topTeamStatsOffensive);

        return transformTablesToMap(defensiveStatsTable, offensiveStatsTable, new ArrayList<>());

    }

    private Document goToPageOfSearchedItem(String itemToSearch, WebDriver driver, String tableName) {
        String baseURL = "https://whoscored.com";
        driver.get(baseURL + "/search/?t=" + itemToSearch);
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        Element table = getTableByTitle(document, tableName);
        Element linkElement = table.selectFirst("a[href]");
        String relativeURL = linkElement.attr("href");
        String fullURL = baseURL + relativeURL;
        driver.get(fullURL);
        return Jsoup.parse(driver.getPageSource());
    }
}
