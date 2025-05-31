package ar.edu.unq.pronostico.deportivo.model.player;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Forward extends Player {

    public Forward(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality, perfomanceStatistics);
    }

    @Override
    public void checkIfHasEverythingToCalculatePerformance() {
        String goals = "Goals";
        String assists = "Assists";
        List<String> requiredStats = Arrays.asList(goals, assists);
        if(hasNotRequiredStats(requiredStats)){
            throw  missingStatsError();
        }
    }

    @Override
    public Double calculate() {
        double goals = Double.parseDouble(getPerformanceStatistics().get("Goals"));
        double assists = Double.parseDouble(getPerformanceStatistics().get("Assists"));
        return (goals / 2) + (assists / 2);
    }
}
