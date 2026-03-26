package dev.uepb.gereciador.ambientes.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuração global da documentação OpenAPI (Swagger) da aplicação.
 *
 * <p>
 * Define as informações gerais da API exibidas no Swagger UI, incluindo título, descrição, versão,
 * dados de contato e servidores disponíveis.
 * </p>
 *
 * <p>
 * A documentação interativa pode ser acessada em: <a href=
 * "http://localhost:8080/swagger-ui/index.html">http://localhost:8080/swagger-ui/index.html</a>
 * </p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Configuration
public class SpringDocConfig {

        /**
         * Configura o objeto {@link OpenAPI} com as metainformações da API.
         *
         * @return instância de {@link OpenAPI} com título, descrição, versão e servidores
         *         configurados
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI().info(new Info().title("Gerenciador de Ambientes UEPB — API")
                                .version("1.0.0")
                                .description("""
                                                API REST para gerenciamento e reserva de ambientes físicos da
                                                Universidade Estadual da Paraíba (UEPB).

                                                ## Autenticação
                                                A maioria dos endpoints requer autenticação via **JWT Bearer Token**.
                                                Para obter o token, utilize o endpoint `POST /auth/login` com suas credenciais.
                                                Em seguida, clique em **Authorize** e insira o token no formato: `Bearer {token}`.

                                                ## Perfis de acesso
                                                | Perfil  | Permissões |
                                                |---------|-----------|
                                                | `USER`  | Realizar e consultar suas próprias reservas |
                                                | `ADMIN` | Gerenciar ambientes, listar usuários e reservas |
                                                | `OWNER` | Acesso total, incluindo criação de administradores |

                                                ## Horário de funcionamento
                                                Reservas são permitidas entre **08:00 e 22:00**, em slots de **1 hora** iniciando em hora cheia.
                                                """)
                                .license(new License().name("MIT License")
                                                .url("https://opensource.org/licenses/MIT")))
                                .servers(List.of(new Server().url("http://localhost:8080")
                                                .description("Servidor local de desenvolvimento")));
        }
}
