package ar.edu.unq.pronostico.deportivo.service.integration;

import ar.edu.unq.pronostico.deportivo.model.Player;
import ar.edu.unq.pronostico.deportivo.utils.AppLogger;
import ar.edu.unq.pronostico.deportivo.utils.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
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
            return data;
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

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
}
