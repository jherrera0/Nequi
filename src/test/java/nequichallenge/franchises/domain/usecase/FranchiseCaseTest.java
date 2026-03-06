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

    // ==================== createFranchise ====================

    @Test
    void createFranchise_shouldCreateFranchise_whenNameIsAvailable() {
        Franchise franchise = new Franchise(null, "Nueva Franquicia", List.of());

        when(franchisePersistencePort.franchiseExistsByName("Nueva Franquicia")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.createFranchise(franchise)).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchisePersistencePort).franchiseExistsByName("Nueva Franquicia");
        verify(franchisePersistencePort).createFranchise(franchise);
    }

    @Test
    void createFranchise_shouldThrowFranchiseAlreadyExistsException_whenNameAlreadyExists() {
        Franchise franchise = new Franchise(null, "Franquicia Existente", List.of());

        when(franchisePersistencePort.franchiseExistsByName("Franquicia Existente")).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .expectError(FranchiseAlreadyExistsException.class)
                .verify();

        verify(franchisePersistencePort).franchiseExistsByName("Franquicia Existente");
        verify(franchisePersistencePort, never()).createFranchise(any());
    }

    @Test
    void createFranchise_shouldReturnEmpty_whenPersistenceReturnsEmpty() {
        Franchise franchise = new Franchise(null, "Franquicia Vacia", List.of());

        when(franchisePersistencePort.franchiseExistsByName("Franquicia Vacia")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.createFranchise(franchise)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .verifyComplete();
    }

    // ==================== updateName ====================

    @Test
    void updateName_shouldUpdateName_whenFranchiseExistsAndNameIsAvailable() {
        Franchise input = new Franchise(1, "Nombre Nuevo", List.of());
        Franchise existing = new Franchise(1, "Nombre Viejo", List.of());
        Franchise updated = new Franchise(1, "Nombre Nuevo", List.of());

        when(franchisePersistencePort.findById(1)).thenReturn(Mono.just(existing));
        when(franchisePersistencePort.franchiseExistsByName("Nombre Nuevo")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.updateFranchise(existing)).thenReturn(Mono.just(updated));

        StepVerifier.create(franchiseCase.updateName(input))
                .expectNextMatches(f -> f.getName().equals("Nombre Nuevo"))
                .verifyComplete();

        verify(franchisePersistencePort).findById(1);
        verify(franchisePersistencePort).franchiseExistsByName("Nombre Nuevo");
        verify(franchisePersistencePort).updateFranchise(existing);
    }

    @Test
    void updateName_shouldThrowFranchiseNameEmptyException_whenNameIsNull() {
        Franchise franchise = mock(Franchise.class);
        when(franchise.getName()).thenReturn(null);

        StepVerifier.create(franchiseCase.updateName(franchise))
                .expectError(FranchiseNameEmptyException.class)
                .verify();

        verify(franchisePersistencePort, never()).findById(any());
        verify(franchisePersistencePort, never()).franchiseExistsByName(any());
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    @Test
    void updateName_shouldThrowFranchiseNameEmptyException_whenNameIsBlank() {
        Franchise franchise = mock(Franchise.class);
        when(franchise.getName()).thenReturn("   ");

        StepVerifier.create(franchiseCase.updateName(franchise))
                .expectError(FranchiseNameEmptyException.class)
                .verify();

        verify(franchisePersistencePort, never()).findById(any());
        verify(franchisePersistencePort, never()).franchiseExistsByName(any());
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    @Test
    void updateName_shouldThrowFranchiseNotFoundException_whenFranchiseDoesNotExist() {
        Franchise franchise = new Franchise(1, "Nombre Nuevo", List.of());

        when(franchisePersistencePort.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseCase.updateName(franchise))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchisePersistencePort).findById(1);
        verify(franchisePersistencePort, never()).franchiseExistsByName(any());
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    @Test
    void updateName_shouldThrowFranchiseNameAlreadyExist_whenNameAlreadyTaken() {
        Franchise input = new Franchise(1, "Nombre Existente", List.of());
        Franchise existing = new Franchise(1, "Nombre Viejo", List.of());

        when(franchisePersistencePort.findById(1)).thenReturn(Mono.just(existing));
        when(franchisePersistencePort.franchiseExistsByName("Nombre Existente")).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseCase.updateName(input))
                .expectError(FranchiseNameAlreadyExist.class)
                .verify();

        verify(franchisePersistencePort).findById(1);
        verify(franchisePersistencePort).franchiseExistsByName("Nombre Existente");
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }
}