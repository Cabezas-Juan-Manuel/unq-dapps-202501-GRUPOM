package ar.edu.unq.pronostico.deportivo.webservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerWithPerformanceScoreDto {
    private String playerName;
    private String age;
    private String nationality;
    private String team;
    private String performanceScore;
}
