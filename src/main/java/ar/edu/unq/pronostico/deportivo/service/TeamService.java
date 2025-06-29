package ar.edu.unq.pronostico.deportivo.service;

import ar.edu.unq.pronostico.deportivo.model.FootballComparer;
import ar.edu.unq.pronostico.deportivo.model.Team;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamService {
    public Team createTeamFromData(String teamName, List<Map<String, String>> teamStats) {
        return new Team(teamName, teamStats);
    }

    public List<Map<String, String>> compareTeams(Team teamOne, Team teamTwo) {

        return FootballComparer.compareTeams(teamOne, teamTwo);

    }
}
