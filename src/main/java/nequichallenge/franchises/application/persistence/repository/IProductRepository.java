package nequichallenge.franchises.application.persistence.repository;

import nequichallenge.franchises.application.persistence.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IProductRepository extends ReactiveCrudRepository<ProductEntity,Integer> {
    Mono<Boolean> existsByName(String name);

    Mono<ProductEntity> findById(Integer id);
}
