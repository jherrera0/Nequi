package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import nequichallenge.franchises.domain.exception.FranchiseNameAlreadyExist;
import nequichallenge.franchises.domain.exception.FranchiseNameEmptyException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.model.Franchise;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseCaseTest {

    @Mock
    private IFranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private FranchiseCase franchiseCase;

    @Test
    void createFranchise_shouldReturnFranchise_whenFranchiseIsValid() {
        Franchise franchise = new Franchise(null, "Test Franchise", List.of());
        when(franchisePersistencePort.createFranchise(franchise)).thenReturn(Mono.just(franchise));
        when(franchisePersistencePort.franchiseExistsByName(franchise.getName())).thenReturn(Mono.just(false));
        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void createFranchise_shouldReturnEmptyMono_whenFranchisePersistencePortReturnsEmpty() {
        Franchise franchise = new Franchise();
        when(franchisePersistencePort.createFranchise(franchise)).thenReturn(Mono.empty());
        when(franchisePersistencePort.franchiseExistsByName(franchise.getName())).thenReturn(Mono.just(false));
        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .verifyComplete();
    }

    @Test
    void createFranchise_returnsError_whenFranchiseAlreadyExists() {
        Franchise franchise = new Franchise(null, "Existing Franchise", List.of());
        when(franchisePersistencePort.franchiseExistsByName(franchise.getName())).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .expectError(FranchiseAlreadyExistsException.class)
                .verify();

        verify(franchisePersistencePort).franchiseExistsByName(franchise.getName());
        verify(franchisePersistencePort, never()).createFranchise(any());
    }
    @Test
    void updateName_shouldUpdateFranchiseName_whenFranchiseExistsAndNameIsValid() {
        Franchise existingFranchise = new Franchise(1, "Old Name", List.of());
        Franchise updatedFranchise = new Franchise(1, "New Name", List.of());

        when(franchisePersistencePort.findById(1)).thenReturn(Mono.just(existingFranchise));
        when(franchisePersistencePort.franchiseExistsByName("New Name")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.updateFranchise(existingFranchise)).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(franchiseCase.updateName(updatedFranchise))
                .expectNextMatches(franchise -> franchise.getName().equals("New Name"))
                .verifyComplete();

        verify(franchisePersistencePort).findById(1);
        verify(franchisePersistencePort).franchiseExistsByName("New Name");
        verify(franchisePersistencePort).updateFranchise(existingFranchise);
    }

    @Test
    void updateName_shouldThrowFranchiseNameEmptyException_whenNameIsNullOrEmpty() {
        Franchise franchiseWithNullName = mock(Franchise.class);
        Franchise franchiseWithEmptyName = mock(Franchise.class);

        when(franchiseWithNullName.getName()).thenReturn(null);
        when(franchiseWithEmptyName.getName()).thenReturn("");

        StepVerifier.create(franchiseCase.updateName(franchiseWithNullName))
                .expectError(FranchiseNameEmptyException.class)
                .verify();

        StepVerifier.create(franchiseCase.updateName(franchiseWithEmptyName))
                .expectError(FranchiseNameEmptyException.class)
                .verify();

        verify(franchisePersistencePort, never()).findById(any());
        verify(franchisePersistencePort, never()).franchiseExistsByName(any());
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    @Test
    void updateName_shouldThrowFranchiseNotFoundException_whenFranchiseDoesNotExist() {
        Franchise nonExistentFranchise = new Franchise(1, "New Name", List.of());

        when(franchisePersistencePort.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseCase.updateName(nonExistentFranchise))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchisePersistencePort).findById(1);
        verify(franchisePersistencePort, never()).franchiseExistsByName(any());
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    @Test
    void updateName_shouldThrowFranchiseNameAlreadyExist_whenNameAlreadyExists() {
        Franchise franchise = new Franchise(1, "Existing Name", List.of());
        Franchise existingFranchise = new Franchise(1, "Old Name", List.of());

        when(franchisePersistencePort.findById(1)).thenReturn(Mono.just(existingFranchise));
        when(franchisePersistencePort.franchiseExistsByName("Existing Name")).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseCase.updateName(franchise))
                .expectError(FranchiseNameAlreadyExist.class)
                .verify();

        verify(franchisePersistencePort).findById(1);
        verify(franchisePersistencePort).franchiseExistsByName("Existing Name");
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }
}