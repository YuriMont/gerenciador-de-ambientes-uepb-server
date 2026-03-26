package dev.uepb.gereciador.ambientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Classe principal da aplicação Gerenciador de Ambientes UEPB.
 *
 * <p>Inicializa o contexto Spring Boot, carrega variáveis de ambiente do arquivo
 * {@code .env} (se presente na raiz do projeto) via {@code dotenv-java}, e habilita
 * o suporte a auditoria automática de datas no MongoDB ({@code @CreatedDate},
 * {@code @LastModifiedDate}).</p>
 *
 * <p>A auto-configuração do DataSource JDBC é desabilitada explicitamente, pois
 * a aplicação utiliza exclusivamente o MongoDB como banco de dados.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@EnableMongoAuditing
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AmbientesApplication {

    /**
     * Ponto de entrada da aplicação.
     *
     * <p>Carrega as variáveis do arquivo {@code .env} antes de iniciar o Spring,
     * garantindo que propriedades como {@code MONGODB_URI} e {@code SECRET} estejam
     * disponíveis como system properties durante a configuração do contexto.</p>
     *
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(env -> System.setProperty(env.getKey(), env.getValue()));

        SpringApplication.run(AmbientesApplication.class, args);
    }
}
