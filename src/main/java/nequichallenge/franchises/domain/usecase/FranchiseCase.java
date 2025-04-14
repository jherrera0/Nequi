package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import nequichallenge.franchises.domain.exception.FranchiseNameAlreadyExist;
import nequichallenge.franchises.domain.exception.FranchiseNameEmptyException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.model.Franchise;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.util.ConstValidations;
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
                    if (exists.compareTo(Boolean.TRUE) == ConstValidations.ZERO) {
                        return Mono.error(new FranchiseAlreadyExistsException());
                    }
                    return franchisePersistencePort.createFranchise(franchise)
                            .flatMap(Mono::just);
                });
    }

    @Override
    public Mono<Franchise> updateName(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isEmpty()) {
            return Mono.error(new FranchiseNameEmptyException());
        }

        return franchisePersistencePort.findById(franchise.getId())
                .flatMap(existedFranchise -> franchisePersistencePort.franchiseExistsByName(franchise.getName())
                        .flatMap(
                                existsName -> {
                                    if (existsName.compareTo(Boolean.TRUE) == ConstValidations.ZERO) {
                                        return Mono.error(new FranchiseNameAlreadyExist());
                                    }
                                    existedFranchise.setName(franchise.getName());
                                    return franchisePersistencePort.updateFranchise(existedFranchise);
                                }
                        ))
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException()));
    }
}
