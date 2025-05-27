package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.aspects.UserActivityWatcher;
import ar.edu.unq.pronostico.deportivo.model.Activity;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import ar.edu.unq.pronostico.deportivo.webservice.Dtos.ActivityDto;
import ar.edu.unq.pronostico.deportivo.webservice.Dtos.ActivityPageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserActivityWatcher userActivityWatcher;

    @GetMapping("activityHistory")
    @Transactional
    @Parameter(example = "carlos")
    @Parameter(example = "0")
    @Operation(summary = "returns a page with the activity of the user", description = "returns a given page of the users activity, if the parameter" +
            " page is not given the default page will be 0, the activity is every private endpoint witch  the user has interacted with")
    public ResponseEntity<ActivityPageDto> activityHistory(@RequestParam String userName, @RequestParam(defaultValue = "0") int page) {
        userActivityWatcher.logUserActivity();
        Page<Activity> userActivityHistory = userService.getUserActivy(userName, page);
        List<ActivityDto> userActivitiesDto = ActivityMapper.toActivityDtoFromActivity(userActivityHistory.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(new ActivityPageDto(userActivitiesDto, userName,
                                                            userActivityHistory.hasPrevious(), userActivityHistory.hasNext()));
    }
}
