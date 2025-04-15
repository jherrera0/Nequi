package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.api.IBranchServicePort;
import nequichallenge.franchises.domain.exception.BranchAlreadyExistException;
import nequichallenge.franchises.domain.exception.BranchNameEmptyException;
import nequichallenge.franchises.domain.exception.BranchNotFoundException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.model.Branch;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
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
        return validateParams(name)
                .flatMap(validName ->
                        franchisePersistencePort.franchiseExistsById(franchiseId)
                                .filter(Boolean::booleanValue)
                                .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
                                .flatMap(ignored ->
                                        branchPersistencePort.existsByName(validName)
                                                .filter(exists -> !exists)
                                                .switchIfEmpty(Mono.error(new BranchAlreadyExistException()))
                                                .flatMap(ignored2 ->
                                                        branchPersistencePort.addBranch(franchiseId, validName)
                                                )
                                )
                );
    }
    @Override
    public Mono<Branch> updateName(Branch branch) {
        return Mono.just(branch)
                .filter(b -> b.getName() != null && !b.getName().isEmpty())
                .switchIfEmpty(Mono.error(new BranchNameEmptyException()))
                .flatMap(validBranch ->
                        branchPersistencePort.findById(validBranch.getId())
                                .switchIfEmpty(Mono.error(new BranchNotFoundException()))
                                .flatMap(existedBranch -> {
                                    existedBranch.setName(validBranch.getName());
                                    return branchPersistencePort.updateBranch(existedBranch);
                                })
                );
    }

    private static Mono<String> validateParams(String name) {
        if (name == null || name.isEmpty()) {
            return Mono.error(new BranchNameEmptyException());
        }
        return Mono.just(name);
    }
}
