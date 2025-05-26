package ar.edu.unq.pronostico.deportivo.model.Player;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Forward extends Player {
    private Map<String, String> performanceStatistics;

    public Forward(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality);
        this.performanceStatistics = perfomanceStatistics;
    }

    @Override
    public void checkIfHasEverythingToCalculatePerformance() {
        String goals = "Goals";
        String assists = "Assists";
        List<String> requiredKeys = Arrays.asList(goals, assists);
        if(!performanceStatistics.keySet().containsAll(requiredKeys)){
            throw  missingStatsError();
        }
    }

    @Override
    public Double calculate() {
        double goals = Double.parseDouble(this.performanceStatistics.get("Goals"));
        double assists = Double.parseDouble(this.performanceStatistics.get("Assists"));
        return (goals / 2) + (assists / 2);
    }
}
