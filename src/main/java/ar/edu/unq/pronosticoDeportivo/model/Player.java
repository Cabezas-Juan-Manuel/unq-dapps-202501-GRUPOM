package ar.edu.unq.pronosticoDeportivo.model;

import lombok.Data;

@Data
public class Player {
        private String nombre;
        private int partidosJugados;
        private int goles;
        private int asistencias;
        private String rating;
}
