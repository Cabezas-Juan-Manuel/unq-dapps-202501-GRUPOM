package ar.edu.unq.pronostico.deportivo.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Endpoint(id = "futureMatchTeamRequests")
public class FutureMatchTeamRequestsActuatorEndpoint {

    private static final String METRIC_NAME = "pronostico.deportivo.future.matches.requests";
    private final MeterRegistry meterRegistry;
    private static final String team_label = "team";
    public FutureMatchTeamRequestsActuatorEndpoint(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @ReadOperation
    public Map<String, Object> getTeamRequestStats() {
        // busca los contadores por equipo del controller y los agrupa por equipo
        Map<String, Double> teamCounts = meterRegistry.find(METRIC_NAME)
                .counters()
                .stream()
                .filter(counter -> Objects.nonNull(counter.getId().getTag(team_label)))
                .collect(Collectors.groupingBy(
                        counter -> counter.getId().getTag(team_label),
                        Collectors.summingDouble(Counter::count)
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("description", "Count of teams queried for future matches");
        result.put("team_request_counts", teamCounts);
        return result;
    }
}