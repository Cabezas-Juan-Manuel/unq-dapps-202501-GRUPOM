package ar.edu.unq.pronostico.deportivo.model.PlayerH;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class Defender extends Player{
    private Map<String, String> performanceStatistics;
    public Defender(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality);
        performanceStatistics = perfomanceStatistics;
    }

    @Override
    public Double calculatePerformance() {
        double fouls = Double.parseDouble(this.performanceStatistics.get("Fouls"));
        double clear = Double.parseDouble(this.performanceStatistics.get("Clear"));
        double inter = Double.parseDouble(this.performanceStatistics.get("Inter"));
        double blocks = Double.parseDouble(this.performanceStatistics.get("Blocks"));
        return clear + inter + blocks - fouls;
    }
}
