package ar.edu.unq.pronostico.deportivo.webservice.Dtos;

import ar.edu.unq.pronostico.deportivo.model.Activity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityPageDto {
    private List<ActivityDto> activityList;
    private String userName;
    private boolean hasPrevious;
    private boolean hasNext;
}
