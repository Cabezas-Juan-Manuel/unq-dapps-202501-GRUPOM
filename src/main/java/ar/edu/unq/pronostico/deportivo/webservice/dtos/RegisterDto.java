package ar.edu.unq.pronostico.deportivo.webservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NotBlank
    private String name;
    @NotBlank
    private String password;
}
