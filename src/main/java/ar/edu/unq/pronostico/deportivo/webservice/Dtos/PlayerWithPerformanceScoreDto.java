package ar.edu.unq.pronostico.deportivo.webservice.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerWithPerformanceScoreDto {
    private String PlayerName;
    private String Age;
    private String Nationality;
    private String Team;
    private String PerformanceScore;
}
