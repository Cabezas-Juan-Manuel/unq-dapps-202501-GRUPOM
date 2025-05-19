package ar.edu.unq.pronostico.deportivo.model;

import lombok.Data;

@Data
public class PlayerForTeam {
        private String name;
        private int matchesPlayed;
        private int goals;
        private int assist;
        private double rating;
}
