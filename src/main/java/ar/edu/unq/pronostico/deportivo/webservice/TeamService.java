package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.model.Team;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamService {
    public Team createTeamFrom(String teamName, List<Map<String, String>> teamStats) {
        return new Team(teamName, teamStats);
    }

    public List<Map<String, String>> compareTeams(Team teamOne, Team teamTwo) {

        return FootballComparator.compareTeams(teamOne, teamTwo);

    }
}
