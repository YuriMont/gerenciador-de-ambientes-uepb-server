package dev.uepb.gereciador.ambientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AmbientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmbientesApplication.class, args);
	}

}
