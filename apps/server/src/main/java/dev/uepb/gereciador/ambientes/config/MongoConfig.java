package dev.uepb.gereciador.ambientes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Configuração do cliente e template MongoDB da aplicação.
 *
 * <p>Define os beans {@link MongoClient} e {@link MongoTemplate} utilizados pelo
 * Spring Data MongoDB. A URI de conexão é carregada a partir da variável de ambiente
 * {@code MONGODB_URI}, podendo ser definida via arquivo {@code .env} na raiz do projeto.</p>
 *
 * <p>Suporta URIs nos formatos {@code mongodb://} e {@code mongodb+srv://} (MongoDB Atlas).</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Configuration
public class MongoConfig {

    /** URI de conexão, vinda da variável de ambiente {@code MONGODB_URI}. */
    private final ConnectionString connectionString;

    public MongoConfig(@Value("${MONGODB_URI}") String mongodbUri) {
        this.connectionString = new ConnectionString(mongodbUri);
    }

    /**
     * Cria o bean {@link MongoClient} com base na URI de conexão configurada.
     *
     * @return instância configurada do {@link MongoClient}
     */
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(connectionString);
    }

    /**
     * Cria o bean {@link MongoTemplate} associado ao banco de dados configurado.
     *
     * <p>O nome do banco é determinado pela propriedade {@code spring.data.mongodb.database}.
     * Caso não esteja configurado, é inferido da URI ou utilizado o valor padrão {@code ambientes}.</p>
     *
     * @param mongoClient o cliente MongoDB
     * @param database    nome do banco (opcional, via {@code spring.data.mongodb.database})
     * @return instância configurada do {@link MongoTemplate}
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient,
            @Value("${spring.data.mongodb.database:}") String database) {
        String dbName = database;
        if (dbName == null || dbName.isBlank()) {
            // Da própria URI de conexão: `System.getProperty` lia propriedade de
            // sistema da JVM, que nunca é setada em produção, e o fallback acabava
            // apontando o template para o banco de teste.
            dbName = connectionString.getDatabase();
            if (dbName == null || dbName.isBlank()) {
                dbName = "ambientes";
            }
        }
        return new MongoTemplate(mongoClient, dbName);
    }
}
