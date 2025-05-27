package ar.edu.unq.pronostico.deportivo.model.Player;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class Defender extends Player{

    public Defender(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality, perfomanceStatistics);
    }

    @Override
    public void checkIfHasEverythingToCalculatePerformance() {
        String fouls = "Fouls";
        String clear = "Clear";
        String inter = "Inter";
        String blocks = "Blocks";
        List<String> requiredStats = Arrays.asList(clear, blocks, fouls, inter);
        if(hasNotRequiredStats(requiredStats)){
            throw  missingStatsError();
        }
    }

    @Override
    public Double calculate() {
        double fouls = Double.parseDouble(getPerformanceStatistics().get("Fouls"));
        double clear = Double.parseDouble(getPerformanceStatistics().get("Clear"));
        double inter = Double.parseDouble(getPerformanceStatistics().get("Inter"));
        double blocks = Double.parseDouble(getPerformanceStatistics().get("Blocks"));
        return clear + inter + blocks - fouls;
    }
}
