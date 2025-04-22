package ar.edu.unq.pronostico.deportivo.webservice.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String name;
    private String password;
}
