package ar.edu.unq.pronostico.deportivo.webservice.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDto {
    private String url;
    private String method;
    private String queryParams;
    private LocalDateTime time;
}
