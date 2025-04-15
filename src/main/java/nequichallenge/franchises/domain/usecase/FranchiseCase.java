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
        return Mono.just(franchise)
                .filter(f -> f.getName() != null && !f.getName().isEmpty())
                .switchIfEmpty(Mono.error(new FranchiseNameEmptyException()))
                .flatMap(validFranchise ->
                        franchisePersistencePort.findById(validFranchise.getId())
                                .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
                                .flatMap(existedFranchise ->
                                        franchisePersistencePort.franchiseExistsByName(validFranchise.getName())
                                                .filter(exists -> exists.compareTo(Boolean.TRUE) != ConstValidations.ZERO)
                                                .switchIfEmpty(Mono.error(new FranchiseNameAlreadyExist()))
                                                .flatMap(exists -> {
                                                    existedFranchise.setName(validFranchise.getName());
                                                    return franchisePersistencePort.updateFranchise(existedFranchise);
                                                })
                                )
                );
    }

}
