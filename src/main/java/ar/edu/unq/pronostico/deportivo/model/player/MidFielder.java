package ar.edu.unq.pronostico.deportivo.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
public class MidFielder extends Player {

    public MidFielder(String name, String age, String team, String nationality, Map<String, String> performanceStatistics){
        super(name, age, team, nationality, performanceStatistics);
    }

    @Override
    public void checkIfHasEverythingToCalculatePerformance() {
        String clear = "Clear";
        String fouls = "Fouls";
        String tackles = "Tackles";
        String assists = "Assists";
        List<String> requiredStats = Arrays.asList(clear, fouls, tackles, assists);
        if (hasNotRequiredStats(requiredStats)){
            throw missingStatsError();
        }
    }

    @Override
    public Double calculate() {
        double clear = Double.parseDouble(getPerformanceStatistics().get("Clear"));
        double fouls = Double.parseDouble(getPerformanceStatistics().get("Fouls"));
        double tackles = Double.parseDouble(getPerformanceStatistics().get("Tackles"));
        double assists = Double.parseDouble(getPerformanceStatistics().get("Assists"));
        return clear + tackles + (assists / 2) - fouls;
    }
}
