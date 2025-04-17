package ar.edu.unq.pronosticodeportivo.service.integration;

import ar.edu.unq.pronosticodeportivo.utils.AppLogger;
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

    private WhoScoredService() {
    }

    private static WebDriver configureWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    private static Map<String, String> generateMapNameId() {
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("team-stats", "top-team-stats-summary-grid");
        dictionary.put("team-players", "top-player-stats-summary-grid");
        dictionary.put("player-stats", "top-player-stats-summary-grid");
        dictionary.put("player-latest-matches", "player-matches-table");
        return dictionary;
    }

    private static Element getTableByTitle(Document doc, String title) {
        Elements h2s = doc.select("h2");
        for (Element h2 : h2s) {
            if (h2.text().toLowerCase().contains(title.toLowerCase())) {
                return h2.nextElementSibling();
            }
        }
        return null;
    }

    private static Element getTableById(Document doc, String id) {
        Element table = doc.selectFirst("[id='" + id + "']");
        if (table == null) {
            AppLogger.error("WhoScoredService", "getTableById", "Stadistics table not found");
            return null;
        }
        return table;
    }

    private static List<Map<String, String>> getTableByIdContent(Element table) {
        Elements tableChildren = table.children();

        if (tableChildren.size() < 2) {
            AppLogger.warn("WhoScoredService", "getTableByIdContent", "The table should have two direct children (head y body)");
            return Collections.emptyList();
        }

        Element head = tableChildren.get(0);
        Element body = tableChildren.get(1);

        Elements headerElements = Objects.requireNonNull(head.firstElementChild()).children();
        List<String> headers = new ArrayList<>();
        for (Element th : headerElements) {
            headers.add(th.text().trim());
        }

        Elements rowElements = body.children();
        return zipTableHeadWithBody(headers, rowElements);
    }

    private static List<Map<String, String>> zipTableHeadWithBody(List<String> headers, Elements rows) {
        List<Map<String, String>> data = new ArrayList<>();
        for (Element row : rows) {
            List<String> values = getValuesFromRow(row);
            Map<String, String> rowDict = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                rowDict.put(headers.get(i), values.get(i));
            }
            data.add(rowDict);
        }
        return data;
    }

    private static List<String> getValuesFromRow(Element row) {
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

    public static String getDataFromTableOnWeb(String text, String searchBy, String tableBy) {
        if (!searchBy.equals("player") && !searchBy.equals("team")) {
            AppLogger.error("WhoScoredService", "getDataFromTableOnWeb", "Invalid value for searchBy. Must be 'player' or 'team'");
            System.exit(1);
        }
        if (!tableBy.equals("team-stats") && !tableBy.equals("team-players") &&
                !tableBy.equals("player-stats") && !tableBy.equals("player-latest-matches")) {
            AppLogger.error("WhoScoredService", "getDataFromTableOnWeb", "Invalid value for tableBy");
            System.exit(1);
        }

        String jsonOutput = null;

        Map<String, String> dicIds = generateMapNameId();

        WebDriver driver = configureWebDriver();

        try {
            String baseURL = "https://whoscored.com";
            driver.get(baseURL + "/search/?t=" + text);
            Document soup = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
            Element table = getTableByTitle(soup, searchBy);

            if (table == null) {
                AppLogger.error("WhoScoredService", "getDataFromTableOnWeb", String.format("Results table not found for %s", text));
                return null;
            }

            Element linkElement = table.selectFirst("a[href]");
            if (linkElement == null) {
                AppLogger.error("WhoScoredService", "getDataFromTableOnWeb", "Link not found in the results table");
                return null;
            }
            String relativeURL = linkElement.attr("href");
            String fullURL = baseURL + relativeURL;
            driver.get(fullURL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(dicIds.get(tableBy))));

            soup = Jsoup.parse(driver.getPageSource());
            Element dataTable = getTableById(soup, dicIds.get(tableBy));
            if (dataTable != null) {
                List<Map<String, String>> data = getTableByIdContent(dataTable);
                if (data != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    jsonOutput = mapper.writeValueAsString(data);
                }
            }

        } catch (Exception e) {
            AppLogger.error("WhoScoredService", "getDataFromTableOnWeb", e.getMessage());
        } finally {
            driver.quit();
        }
        return jsonOutput;
    }
}
