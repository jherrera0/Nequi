package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.api.IBranchServicePort;
import nequichallenge.franchises.domain.exception.BranchAlreadyExistException;
import nequichallenge.franchises.domain.exception.BranchNameEmptyException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.model.Branch;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.util.ConstValidations;
import reactor.core.publisher.Mono;

public class BranchCase implements IBranchServicePort {
    private final IBranchPersistencePort branchPersistencePort;
    private final IFranchisePersistencePort franchisePersistencePort;
    public BranchCase(IBranchPersistencePort branchPersistencePort,
                      IFranchisePersistencePort franchisePersistencePort) {
        this.branchPersistencePort = branchPersistencePort;
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Branch> addBranch(Integer franchiseId, String name) {
        Mono<Branch> error = validateParams(name);
        if (error != null) return error;
        return franchisePersistencePort.franchiseExistsById(franchiseId)
                .flatMap(
                        exists -> {
                            if (exists.compareTo(Boolean.FALSE) == ConstValidations.ZERO) {
                                return Mono.error(new FranchiseNotFoundException());
                            }
                            return branchPersistencePort.existsByName(name)
                                    .flatMap(existsName -> {
                                        if (existsName.compareTo(Boolean.TRUE) == ConstValidations.ZERO) {
                                            return Mono.error(new BranchAlreadyExistException());
                                        }
                                        return branchPersistencePort.addBranch(franchiseId, name);
                                    });
                        }
                );
    }

    private static Mono<Branch> validateParams(String name) {
        if (name == null || name.isEmpty()) {
            return Mono.error(new BranchNameEmptyException());
        }
        return null;
    }
}
