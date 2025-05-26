package ar.edu.unq.pronostico.deportivo.model.Player;

import ar.edu.unq.pronostico.deportivo.service.Errors.UserErrors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.UnknownServiceException;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class Player {
    private String name;
    private String age;
    private String team;
    private String nationality;

    public Double calculatePerformance(){
        checkIfHasEverythingToCalculatePerformance();
        return calculate();
    }

    public abstract void checkIfHasEverythingToCalculatePerformance();

    public abstract Double calculate();

    public RuntimeException missingStatsError(){
        return new RuntimeException(UserErrors.MISSING_STATISTICS_ERROR.getMessage());
    }


}
