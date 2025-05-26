package ar.edu.unq.pronostico.deportivo.model.Player;

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
    private Map<String, String> defensivePerformanceStatistics;
    private Map<String, String> offensivePerformanceStatistics;

    public MidFielder(String name, String age, String team, String nationality, Map<String, String> defensivePerformanceStatistics, Map<String, String> offensivePerformanceStatistics){
        super(name, age, team, nationality);
        this.defensivePerformanceStatistics = defensivePerformanceStatistics;
        this.offensivePerformanceStatistics = offensivePerformanceStatistics;
    }

    @Override
    public void checkIfHasEverythingToCalculatePerformance() {
        String clear = "Clear";
        String fouls = "Fouls";
        String tackles = "Tackles";
        String assists = "Assists";
        List<String> requiredKeysForDeffensiveStatistics = Arrays.asList(clear, fouls, tackles);
        if (!(defensivePerformanceStatistics.keySet().containsAll(requiredKeysForDeffensiveStatistics) && offensivePerformanceStatistics.containsKey(assists))){
            throw missingStatsError();
        }
    }

    @Override
    public Double calculate() {
        double clear = Double.parseDouble(this.defensivePerformanceStatistics.get("Clear"));
        double fouls = Double.parseDouble(this.defensivePerformanceStatistics.get("Fouls"));
        double tackles = Double.parseDouble(this.defensivePerformanceStatistics.get("Tackles"));
        int assists = Integer.parseInt(this.offensivePerformanceStatistics.get("Assists"));
        return clear + tackles + (assists / 2) - fouls;
    }
}
