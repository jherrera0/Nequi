package nequichallenge.franchises.application.persistence.repository;

import nequichallenge.franchises.application.persistence.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IBranchRepository extends ReactiveCrudRepository<BranchEntity, Integer> {
    Mono<Boolean> existsByName(String name);
}
