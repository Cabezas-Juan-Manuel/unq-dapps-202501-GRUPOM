package ar.edu.unq.pronostico.deportivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class PronosticoDeportivoApplication {

	public static void main(String[] args) {

		SpringApplication.run(PronosticoDeportivoApplication.class, args);
	}

}
