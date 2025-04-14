package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.exception.BranchAlreadyExistException;
import nequichallenge.franchises.domain.exception.BranchNameEmptyException;
import nequichallenge.franchises.domain.exception.BranchNotFoundException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.model.Branch;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BranchCaseTest {

    private IBranchPersistencePort branchPersistencePort;
    private IFranchisePersistencePort franchisePersistencePort;
    private BranchCase branchCase;

    @BeforeEach
    void setUp() {
        branchPersistencePort = Mockito.mock(IBranchPersistencePort.class);
        franchisePersistencePort = Mockito.mock(IFranchisePersistencePort.class);
        branchCase = new BranchCase(branchPersistencePort, franchisePersistencePort);
    }

    @Test
    @DisplayName("addBranch should return Branch when franchise exists and name is unique")
    void addBranchReturnsBranchWhenFranchiseExistsAndNameIsUnique() {
        when(franchisePersistencePort.franchiseExistsById(1)).thenReturn(Mono.just(true));
        when(branchPersistencePort.existsByName("BranchName")).thenReturn(Mono.just(false));
        when(branchPersistencePort.addBranch(1, "BranchName")).thenReturn(Mono.just(new Branch()));

        StepVerifier.create(branchCase.addBranch(1, "BranchName"))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(franchisePersistencePort).franchiseExistsById(1);
        verify(branchPersistencePort).existsByName("BranchName");
        verify(branchPersistencePort).addBranch(1, "BranchName");
    }

    @Test
    @DisplayName("addBranch should throw FranchiseNotFoundException when franchise does not exist")
    void addBranchThrowsFranchiseNotFoundExceptionWhenFranchiseDoesNotExist() {
        when(franchisePersistencePort.franchiseExistsById(1)).thenReturn(Mono.just(false));

        StepVerifier.create(branchCase.addBranch(1, "BranchName"))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchisePersistencePort).franchiseExistsById(1);
        verify(branchPersistencePort, never()).existsByName(any());
        verify(branchPersistencePort, never()).addBranch(any(), any());
    }

    @Test
    @DisplayName("addBranch should throw BranchAlreadyExistException when branch name already exists")
    void addBranchThrowsBranchAlreadyExistExceptionWhenBranchNameAlreadyExists() {
        when(franchisePersistencePort.franchiseExistsById(1)).thenReturn(Mono.just(true));
        when(branchPersistencePort.existsByName("BranchName")).thenReturn(Mono.just(true));

        StepVerifier.create(branchCase.addBranch(1, "BranchName"))
                .expectError(BranchAlreadyExistException.class)
                .verify();

        verify(franchisePersistencePort).franchiseExistsById(1);
        verify(branchPersistencePort).existsByName("BranchName");
        verify(branchPersistencePort, never()).addBranch(any(), any());
    }

    @Test
    @DisplayName("addBranch should throw BranchNameEmptyException when name is null or empty")
    void addBranchThrowsBranchNameEmptyExceptionWhenNameIsNullOrEmpty() {
        StepVerifier.create(branchCase.addBranch(1, null))
                .expectError(BranchNameEmptyException.class)
                .verify();

        StepVerifier.create(branchCase.addBranch(1, ""))
                .expectError(BranchNameEmptyException.class)
                .verify();

        verify(franchisePersistencePort, never()).franchiseExistsById(any());
        verify(branchPersistencePort, never()).existsByName(any());
        verify(branchPersistencePort, never()).addBranch(any(), any());
    }
    @Test
    @DisplayName("updateName should update branch name when branch exists and name is valid")
    void updateNameUpdatesBranchNameWhenBranchExistsAndNameIsValid() {
        Branch existingBranch = new Branch(1, "OldName", List.of());
        Branch updatedBranch = new Branch(1, "NewName", List.of());

        when(branchPersistencePort.findById(1)).thenReturn(Mono.just(existingBranch));
        when(branchPersistencePort.updateBranch(existingBranch)).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(branchCase.updateName(updatedBranch))
                .expectNextMatches(branch -> branch.getName().equals("NewName"))
                .verifyComplete();

        verify(branchPersistencePort).findById(1);
        verify(branchPersistencePort).updateBranch(existingBranch);
    }

    @Test
    @DisplayName("updateName should throw BranchNameEmptyException when name is null or empty")
    void updateNameThrowsBranchNameEmptyExceptionWhenNameIsNullOrEmpty() {
        Branch branchWithNullName = new Branch(1, null, List.of());
        Branch branchWithEmptyName = new Branch(1, "", List.of());

        StepVerifier.create(branchCase.updateName(branchWithNullName))
                .expectError(BranchNameEmptyException.class)
                .verify();

        StepVerifier.create(branchCase.updateName(branchWithEmptyName))
                .expectError(BranchNameEmptyException.class)
                .verify();

        verify(branchPersistencePort, never()).findById(any());
        verify(branchPersistencePort, never()).updateBranch(any());
    }

    @Test
    @DisplayName("updateName should throw BranchNotFoundException when branch does not exist")
    void updateNameThrowsBranchNotFoundExceptionWhenBranchDoesNotExist() {
        Branch nonExistentBranch = new Branch(1, "NewName", List.of());

        when(branchPersistencePort.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(branchCase.updateName(nonExistentBranch))
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(branchPersistencePort).findById(1);
        verify(branchPersistencePort, never()).updateBranch(any());
    }
}