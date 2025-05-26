package ar.edu.unq.pronostico.deportivo.model.Player;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class Defender extends Player{
    private Map<String, String> performanceStatistics;
    public Defender(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality);
        performanceStatistics = perfomanceStatistics;
    }

    @Override
    public void checkIfHasEverythingToCalculatePerformance() {
        String fouls = "Fouls";
        String clear = "Clear";
        String inter = "Inter";
        String blocks = "Blocks";
        List<String> requiredKeys = Arrays.asList(clear, blocks, fouls, inter);
        if(!performanceStatistics.keySet().containsAll(requiredKeys)){
            throw  missingStatsError();
        }
    }

    @Override
    public Double calculate() {
        double fouls = Double.parseDouble(this.performanceStatistics.get("Fouls"));
        double clear = Double.parseDouble(this.performanceStatistics.get("Clear"));
        double inter = Double.parseDouble(this.performanceStatistics.get("Inter"));
        double blocks = Double.parseDouble(this.performanceStatistics.get("Blocks"));
        return clear + inter + blocks - fouls;
    }
}
