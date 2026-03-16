package dev.uepb.gereciador.ambientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import io.github.cdimascio.dotenv.Dotenv;


@EnableMongoAuditing
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AmbientesApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(env -> System.setProperty(env.getKey(), env.getValue()));

		SpringApplication.run(AmbientesApplication.class, args);
	}

}
