package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.model.Activity;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.ActivityDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityMapper {

    private ActivityMapper(){}

    public static List<ActivityDto> toActivityDtoFromActivity(List<Activity> activityList) {
        return activityList.stream()
                .map(ActivityMapper :: toActivityDto).toList();
    }

    private static ActivityDto toActivityDto(Activity activity) {
        return new ActivityDto(activity.getUrl(), activity.getMethod(), activity.getQueryParams(), activity.getTime());
    }
}
