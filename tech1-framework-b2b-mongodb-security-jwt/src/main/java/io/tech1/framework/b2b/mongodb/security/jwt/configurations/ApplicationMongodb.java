package io.tech1.framework.b2b.mongodb.security.jwt.configurations;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "io.tech1.framework.b2b.mongodb.security.jwt.repositories",
        mongoTemplateRef = "tech1MongoTemplate"
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationMongodb {

    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Bean(name = "tech1MongoClient")
    public MongoClient tech1MongoClient() {
        var mongodb = this.applicationFrameworkProperties.getSecurityJwtConfigs().getMongodb();
        var connectionString = new ConnectionString(mongodb.connectionString());
        var mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean(name = "tech1MongoDatabaseFactory")
    public MongoDatabaseFactory tech1MongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(
                this.tech1MongoClient(),
                this.applicationFrameworkProperties.getSecurityJwtConfigs().getMongodb().getDatabase()
        );
    }

    @Qualifier(value = "tech1MongoTemplate")
    @Bean(name = "tech1MongoTemplate")
    public MongoTemplate tech1MongoTemplate() {
        var dbRefResolver = new DefaultDbRefResolver(this.tech1MongoDatabaseFactory());
        var mongoConverter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        return new MongoTemplate(
                this.tech1MongoDatabaseFactory(),
                mongoConverter
        );
    }
}
