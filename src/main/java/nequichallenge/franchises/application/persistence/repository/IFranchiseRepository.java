package nequichallenge.franchises.application.persistence.repository;

import nequichallenge.franchises.application.persistence.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IFranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Integer> {
    Mono<Boolean> existsByName(String name);
}
