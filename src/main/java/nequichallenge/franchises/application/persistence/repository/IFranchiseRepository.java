package nequichallenge.franchises.application.persistence.repository;

import nequichallenge.franchises.application.persistence.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IFranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, UUID> {
    Mono<Boolean> existsByName(String name);
}
