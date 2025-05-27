package ar.edu.unq.pronostico.deportivo.model.Player;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Goalkeeper extends Player {

    public Goalkeeper(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality, perfomanceStatistics);
    }

    @Override
    public void checkIfHasEverythingToCalculatePerformance() {
        String clear = "Clear";
        String blocks = "Blocks";
        String fouls = "Fouls";
        List<String> requiredStats = Arrays.asList(clear, blocks, fouls);
        if (hasNotRequiredStats(requiredStats)){
            throw missingStatsError();
        }
    }

    @Override
    public Double calculate() {
        double clear = Double.parseDouble(getPerformanceStatistics().get("Clear"));
        double blocks = Double.parseDouble(getPerformanceStatistics().get("Blocks"));
        double fouls = Double.parseDouble(getPerformanceStatistics().get("Fouls"));
        return clear + blocks - fouls;
    }
}
