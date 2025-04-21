package nequichallenge.franchises.application.configuration;

import lombok.AllArgsConstructor;
import nequichallenge.franchises.infrastructure.adapters.persistence.adapter.BranchAdapter;
import nequichallenge.franchises.infrastructure.adapters.persistence.adapter.FranchiseAdapter;
import nequichallenge.franchises.infrastructure.adapters.persistence.adapter.ProductAdapter;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IBranchEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IFranchiseEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IProductEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IBranchRepository;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IFranchiseRepository;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IProductRepository;
import nequichallenge.franchises.domain.api.IBranchServicePort;
import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import nequichallenge.franchises.domain.api.IProductServicePort;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.spi.IProductPersistencePort;
import nequichallenge.franchises.domain.usecase.BranchCase;
import nequichallenge.franchises.domain.usecase.FranchiseCase;
import nequichallenge.franchises.domain.usecase.ProductCase;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@AllArgsConstructor
public class BeanConfiguration {
    private final IFranchiseEntityMapper franchiseEntityMapper;
    private final IFranchiseRepository franchiseRepository;
    private final IBranchEntityMapper branchEntityMapper;
    private final IBranchRepository branchRepository;
    private final IProductEntityMapper productEntityMapper;
    private final IProductRepository productRepository;

    @Bean
    public IFranchiseServicePort franchiseService() {
        return new FranchiseCase(franchisePersistencePort());
    }

    @Bean
    public IFranchisePersistencePort franchisePersistencePort() {
        return new FranchiseAdapter(franchiseEntityMapper, franchiseRepository);
    }
    @Bean
    public IBranchServicePort branchService() {
        return new BranchCase(branchPersistencePort(),franchisePersistencePort());
    }

    @Bean
    public IBranchPersistencePort branchPersistencePort() {
        return new BranchAdapter(branchRepository,branchEntityMapper);
    }

    @Bean
    public IProductServicePort productService() {
        return new ProductCase(productPersistencePort(),branchPersistencePort(),franchisePersistencePort());
    }

    @Bean
    public IProductPersistencePort productPersistencePort() {
        return new ProductAdapter(productRepository,productEntityMapper);
    }

    @Bean
    public ApplicationRunner initializer(DatabaseClient client) {
        return args -> client.sql("""
        CREATE TABLE IF NOT EXISTS franchise (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            CONSTRAINT uk_franchise_name UNIQUE (name)
        );
        CREATE TABLE IF NOT EXISTS branch (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            franchise_id BIGINT NOT NULL,
            CONSTRAINT fk_branch_franchise FOREIGN KEY (franchise_id) REFERENCES franchise(id) ON DELETE CASCADE
        );
    CREATE TABLE IF NOT EXISTS product (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            isActive BOOLEAN NOT NULL,
            stock INT NOT NULL,
            branch_id BIGINT NOT NULL,
            CONSTRAINT fk_branch_product FOREIGN KEY (branch_id) REFERENCES branch(id) ON DELETE CASCADE
        );
    
    """).fetch().rowsUpdated().subscribe();
    }


}
