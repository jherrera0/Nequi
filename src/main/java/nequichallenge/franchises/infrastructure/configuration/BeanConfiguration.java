package nequichallenge.franchises.infrastructure.configuration;

import lombok.AllArgsConstructor;
import nequichallenge.franchises.application.persistence.adapter.FranchiseAdapter;
import nequichallenge.franchises.application.persistence.mapper.IFranchiseEntityMapper;
import nequichallenge.franchises.application.persistence.repository.IFranchiseRepository;
import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.usecase.FranchiseCase;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@AllArgsConstructor
public class BeanConfiguration {
    private final IFranchiseEntityMapper franchiseEntityMapper;
    private final IFranchiseRepository franchiseRepository;

    @Bean
    public IFranchiseServicePort franchiseService() {
        return new FranchiseCase(franchisePersistencePort());
    }

    @Bean
    public IFranchisePersistencePort franchisePersistencePort() {
        return new FranchiseAdapter(franchiseEntityMapper, franchiseRepository);
    }
    @Bean
    public ApplicationRunner initializer(DatabaseClient client) {
        return args -> client.sql("""
        CREATE TABLE IF NOT EXISTS franchise (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            CONSTRAINT uk_franchise_name UNIQUE (name)
        )
    """).fetch().rowsUpdated().subscribe();
    }

}
