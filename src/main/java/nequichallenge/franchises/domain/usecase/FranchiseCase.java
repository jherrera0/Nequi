package nequichallenge.franchises.domain.usecase;

import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import nequichallenge.franchises.domain.exception.FranchiseNameAlreadyExist;
import nequichallenge.franchises.domain.exception.FranchiseNameEmptyException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.model.Franchise;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.util.ReactiveLogger;
import reactor.core.publisher.Mono;

@Slf4j
public class FranchiseCase implements IFranchiseServicePort {

    private final IFranchisePersistencePort franchisePersistencePort;

    public FranchiseCase(IFranchisePersistencePort franchisePersistencePort) {
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return checkNameAvailability(franchise.getName(), new FranchiseAlreadyExistsException())
                .then(Mono.defer(() -> franchisePersistencePort.createFranchise(franchise)))
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Franquicia creada exitosamente", "Error creando franquicia"));
    }

    @Override
    public Mono<Franchise> updateName(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isBlank()) {
            return Mono.error(new FranchiseNameEmptyException());
        }
        return franchisePersistencePort.findById(franchise.getId())
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
                .flatMap(existing -> checkNameAvailability(franchise.getName(), new FranchiseNameAlreadyExist())
                        .thenReturn(existing))
                .flatMap(existing -> {
                    existing.setName(franchise.getName());
                    return franchisePersistencePort.updateFranchise(existing);
                })
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Nombre de franquicia actualizado exitosamente", "Error actualizando nombre de franquicia"));
    }

    private Mono<Void> checkNameAvailability(String name, RuntimeException errorIfExists) {
        return franchisePersistencePort.franchiseExistsByName(name)
                .filter(exists -> !Boolean.TRUE.equals(exists))
                .switchIfEmpty(Mono.error(errorIfExists))
                .then();
    }
}
