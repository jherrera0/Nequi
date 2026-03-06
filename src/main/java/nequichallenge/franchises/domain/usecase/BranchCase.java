package nequichallenge.franchises.domain.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.domain.api.IBranchServicePort;
import nequichallenge.franchises.domain.exception.BranchAlreadyExistException;
import nequichallenge.franchises.domain.exception.BranchNameEmptyException;
import nequichallenge.franchises.domain.exception.BranchNotFoundException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.model.Branch;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.util.ReactiveLogger;
import reactor.core.publisher.Mono;

/**
 * Case de uso para operaciones sobre sucursales.
 * Implementa el contrato IBranchServicePort y aplica principios SOLID.
 * Responsabilidades:
 * - Orquestar la lógica de negocio para sucursales
 * - Validar reglas de negocio
 * - Coordinar entre puertos de persistencia
 */
@Slf4j
@RequiredArgsConstructor
public class BranchCase implements IBranchServicePort {

    private final IBranchPersistencePort branchPersistencePort;
    private final IFranchisePersistencePort franchisePersistencePort;

    /**
     * Añade una nueva sucursal a una franquicia.
     *
     * @param franchiseId ID de la franquicia
     * @param name Nombre de la sucursal
     * @return Mono con la sucursal creada
     */
    @Override
    public Mono<Branch> addBranch(Integer franchiseId, String name) {
        return validateBranchName(name)
                .flatMap(validName -> validateFranchiseExists(franchiseId)
                        .then(Mono.defer(() -> validateBranchNameNotDuplicated(validName)))
                        .then(Mono.defer(() -> branchPersistencePort.addBranch(franchiseId, validName)))
                )
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Sucursal agregada exitosamente", "Error agregando sucursal"));
    }

    @Override
    public Mono<Branch> updateName(Branch branch) {
        return validateBranchForUpdate(branch)
                .flatMap(validBranch -> retrieveExistingBranch(validBranch.getId()))
                .flatMap(existing -> {
                    existing.setName(branch.getName().trim());
                    return branchPersistencePort.updateBranch(existing);
                })
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Nombre de sucursal actualizado", "Error actualizando nombre de sucursal"));
    }

    // ==================== VALIDACIONES ====================

    /**
     * Valida que el nombre de la sucursal sea válido.
     */
    private Mono<String> validateBranchName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Mono.error(new BranchNameEmptyException());
        }
        return Mono.just(name.trim());
    }

    /**
     * Valida que una sucursal sea válida para actualización.
     * Incluye validación de nombre y presencia de ID.
     */
    private Mono<Branch> validateBranchForUpdate(Branch branch) {
        if (branch == null) {
            return Mono.error(new IllegalArgumentException("La sucursal no puede ser nula"));
        }
        if (branch.getId() == null || branch.getId() <= 0) {
            return Mono.error(new IllegalArgumentException("La sucursal debe tener un ID válido"));
        }
        if (branch.getName() == null || branch.getName().trim().isEmpty()) {
            return Mono.error(new BranchNameEmptyException());
        }
        return Mono.just(branch);
    }

    /**
     * Valida que la franquicia existe.
     */
    private Mono<Void> validateFranchiseExists(Integer franchiseId) {
        return franchisePersistencePort.franchiseExistsById(franchiseId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
                .then();
    }

    private Mono<Void> validateBranchNameNotDuplicated(String name) {
        return branchPersistencePort.existsByName(name)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BranchAlreadyExistException()))
                .then();
    }

    private Mono<Branch> retrieveExistingBranch(Integer branchId) {
        return branchPersistencePort.findById(branchId)
                .switchIfEmpty(Mono.error(new BranchNotFoundException()));
    }
}
