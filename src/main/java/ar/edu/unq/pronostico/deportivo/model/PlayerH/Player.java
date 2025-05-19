package ar.edu.unq.pronostico.deportivo.model.PlayerH;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class Player {
    private String name;
    private String age;
    private String team;
    private String nationality;

    public abstract Double calculatePerformance();
}
