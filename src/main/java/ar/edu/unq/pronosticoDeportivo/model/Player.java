package ar.edu.unq.pronosticoDeportivo.model;

import lombok.Data;

@Data
public class Player {
        private String name;
        private int matchesPlayed;
        private int goals;
        private int assist;
        private double rating;
}
