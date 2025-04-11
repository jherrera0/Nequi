package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import nequichallenge.franchises.domain.model.Franchise;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import reactor.core.publisher.Mono;

public class FranchiseCase implements IFranchiseServicePort {

    private final IFranchisePersistencePort franchisePersistencePort;
    public FranchiseCase(IFranchisePersistencePort franchisePersistencePort) {
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchisePersistencePort.franchiseExistsByName(franchise.getName())
                .flatMap(exists -> {
                    if (exists.compareTo(Boolean.TRUE) == 0) {
                        return Mono.error(new FranchiseAlreadyExistsException());
                    }
                    return franchisePersistencePort.createFranchise(franchise)
                            .flatMap(Mono::just);
                });
    }
}
