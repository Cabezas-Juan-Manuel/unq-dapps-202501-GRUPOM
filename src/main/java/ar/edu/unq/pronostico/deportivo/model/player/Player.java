package ar.edu.unq.pronostico.deportivo.model.player;

import ar.edu.unq.pronostico.deportivo.service.errors.UserErrors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class Player {
    private String name;
    private String age;
    private String team;
    private String nationality;
    private Map<String, String> performanceStatistics;

    public Double calculatePerformance(){
        checkIfHasEverythingToCalculatePerformance();
        return calculate();
    }

    public abstract void checkIfHasEverythingToCalculatePerformance();

    public abstract Double calculate();

    public RuntimeException missingStatsError(){
        return new IllegalArgumentException(UserErrors.MISSING_STATISTICS_ERROR.getMessage());
    }

    public boolean hasNotRequiredStats(List<String> requiredKeys){
        return performanceStatistics == null || !performanceStatistics.keySet().containsAll(requiredKeys);
    }
}
