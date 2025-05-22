package ar.edu.unq.pronostico.deportivo.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("${open-router.api.key}")
    private String apiKey;

    @Value("${open-router.model}")
    private String model;

    private final WebClient webClient;

    public ChatService(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000) // 10 segundos conexión
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60))   // lectura
                        .addHandlerLast(new WriteTimeoutHandler(60))); // escritura

        this.webClient = builder
                .baseUrl("https://openrouter.ai/api/v1")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<String> getResponse(String prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "http://localhost")
                .header("X-Title", "MiAppSpringChat")
                .bodyValue(Map.of(
                        "model", model,
                        "messages", new Object[]{
                                Map.of("role", "user", "content", prompt)
                        }
                ))
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> response = mapper.readValue(json, Map.class);
                        var choices = (List<Map<String, Object>>) response.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> message = (Map<String, Object>) choices.getFirst().get("message");
                            return message.get("content").toString();
                        } else {
                            return "No se obtuvo respuesta del modelo.";
                        }
                    } catch (Exception e) {
                        return "Error al interpretar la respuesta del modelo: " + e.getMessage();
                    }
                });
    }
}
