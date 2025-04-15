package nequichallenge.franchises.infrastructure.adapters.persistence.repository;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IProductRepository extends ReactiveCrudRepository<ProductEntity,Integer> {
    Mono<Boolean> existsByName(String name);

    Mono<ProductEntity> findById(Integer id);
    Mono<ProductEntity> findFirstByBranchIdAndIsActiveTrueOrderByStockDesc(Integer branchId, Boolean isActive);
}
