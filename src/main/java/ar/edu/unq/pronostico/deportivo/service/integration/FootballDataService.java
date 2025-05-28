package ar.edu.unq.pronostico.deportivo.service.integration;

import ar.edu.unq.pronostico.deportivo.service.integration.dataObject.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FootballDataService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${api.external.football-data.url}")
    private String BASE_URL;

    @Value("${api.external.football-data.token}")
    private String AUTH_TOKEN;

    public List<Match> getFuturesMatches(String teamName) {
        LocalDate today = LocalDate.now();
        LocalDate tenDaysLater = today.plusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        String dateFrom = today.format(formatter);
        String dateTo = tenDaysLater.format(formatter);

        String apiUrl = BASE_URL + String.format(
                "/matches?status=SCHEDULED&dateFrom=%s&dateTo=%s",
                dateFrom,
                dateTo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-auth-token", AUTH_TOKEN);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ExternalApiResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                ExternalApiResponse.class
        );

        List<Match> allMatches = response.getBody().matches();

        return allMatches.stream().filter(match ->
                match.homeTeam().name().equalsIgnoreCase(teamName) ||
                match.awayTeam().name().equalsIgnoreCase(teamName))
                .toList();
    }

}
