package dev.uepb.gereciador.ambientes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient(@Value("${MONGODB_URI}") String mongodbUri) {
        // Use ConnectionString to support mongodb+srv and proper parsing of options.
        return MongoClients.create(new ConnectionString(mongodbUri));
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient,
            @Value("${spring.data.mongodb.database:}") String database) {
        String dbName = database;
        if (dbName == null || dbName.isBlank()) {
            // If not explicitly configured, try to infer from URI; fall back to 'ambientes'.
            ConnectionString connectionString = new ConnectionString(
                    System.getProperty("MONGODB_URI", "mongodb://localhost:27017/ambientes_test"));
            dbName = connectionString.getDatabase();
            if (dbName == null || dbName.isBlank()) {
                dbName = "ambientes";
            }
        }
        return new MongoTemplate(mongoClient, dbName);
    }
}
