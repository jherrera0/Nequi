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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchCaseTest {

    @Mock
    private IBranchPersistencePort branchPersistencePort;

    @Mock
    private IFranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private BranchCase branchCase;

    private Branch sampleBranch;

    @BeforeEach
    void setUp() {
        sampleBranch = new Branch(1, "Sucursal Central", new ArrayList<>());
    }

    // ==================== addBranch Tests ====================

    @Test
    @DisplayName("Debe crear una sucursal exitosamente")
    void addBranch_Success() {
        // Arrange
        when(franchisePersistencePort.franchiseExistsById(anyInt())).thenReturn(Mono.just(true));
        when(branchPersistencePort.existsByName(anyString())).thenReturn(Mono.just(false));
        when(branchPersistencePort.addBranch(anyInt(), anyString())).thenReturn(Mono.just(sampleBranch));

        // Act & Assert
        StepVerifier.create(branchCase.addBranch(1, "Nueva Sucursal"))
                .expectNext(sampleBranch)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe fallar cuando la franquicia no existe")
    void addBranch_FranchiseNotFound_Error() {
        // Arrange
        when(franchisePersistencePort.franchiseExistsById(anyInt())).thenReturn(Mono.just(false));

        // No es estrictamente necesario mockear existsByName si el flujo corta antes,
        // pero en versiones antiguas de Mockito/Reactor, inicializarlo con Mono.empty() evita NPEs preventivos.

        // Act & Assert
        StepVerifier.create(branchCase.addBranch(1, "Sucursal"))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(branchPersistencePort, never()).addBranch(anyInt(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar BranchAlreadyExistException si el nombre ya existe")
    void addBranch_DuplicateName_Error() {
        // Arrange
        when(franchisePersistencePort.franchiseExistsById(anyInt())).thenReturn(Mono.just(true));
        // Aquí estaba el error: Mockito devolvía NULL. Ahora devuelve un Mono válido.
        when(branchPersistencePort.existsByName(anyString())).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(branchCase.addBranch(1, "Duplicada"))
                .expectError(BranchAlreadyExistException.class)
                .verify();
    }

    // ==================== updateName Tests ====================

    @Test
    @DisplayName("Debe lanzar BranchNotFoundException al actualizar ID inexistente")
    void updateName_NotFound_Error() {
        // Arrange
        Branch updateInfo = new Branch(99, "Nuevo Nombre", new ArrayList<>());
        // Mockeamos el retorno como Mono.empty() (señal de no encontrado)
        when(branchPersistencePort.findById(anyInt())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(branchCase.updateName(updateInfo))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe fallar por validación de ID inválido")
    void updateName_InvalidId_Error() {
        Branch branch = new Branch(0, "Nombre", new ArrayList<>());

        // No requiere mocks porque la validación ocurre antes de llamar a los puertos
        StepVerifier.create(branchCase.updateName(branch))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe actualizar el nombre correctamente aplicando trim")
    void updateName_Success() {
        Branch input = new Branch(1, "  Sucursal Nueva  ", new ArrayList<>());
        Branch existing = new Branch(1, "Nombre Viejo", new ArrayList<>());
        Branch updated = new Branch(1, "Sucursal Nueva", new ArrayList<>());

        when(branchPersistencePort.findById(1)).thenReturn(Mono.just(existing));
        when(branchPersistencePort.updateBranch(existing)).thenReturn(Mono.just(updated));

        StepVerifier.create(branchCase.updateName(input))
                .expectNextMatches(b -> b.getName().equals("Sucursal Nueva"))
                .verifyComplete();

        verify(branchPersistencePort).updateBranch(argThat(b -> b.getName().equals("Sucursal Nueva")));
    }

    @Test
    @DisplayName("Debe lanzar BranchNameEmptyException cuando el nombre es nulo")
    void updateName_NullName_Error() {
        Branch branch = new Branch(1, null, new ArrayList<>());

        StepVerifier.create(branchCase.updateName(branch))
                .expectError(BranchNameEmptyException.class)
                .verify();

        verify(branchPersistencePort, never()).findById(any());
        verify(branchPersistencePort, never()).updateBranch(any());
    }

    @Test
    @DisplayName("Debe lanzar BranchNameEmptyException cuando el nombre está en blanco")
    void updateName_BlankName_Error() {
        Branch branch = new Branch(1, "   ", new ArrayList<>());

        StepVerifier.create(branchCase.updateName(branch))
                .expectError(BranchNameEmptyException.class)
                .verify();

        verify(branchPersistencePort, never()).findById(any());
        verify(branchPersistencePort, never()).updateBranch(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando la sucursal es nula")
    void updateName_NullBranch_Error() {
        StepVerifier.create(branchCase.updateName(null))
                .expectErrorMatches(e ->
                        e instanceof IllegalArgumentException &&
                        e.getMessage().equals("La sucursal no puede ser nula"))
                .verify();

        verify(branchPersistencePort, never()).findById(any());
        verify(branchPersistencePort, never()).updateBranch(any());
    }

    // ==================== addBranch — validación de nombre ====================

    @Test
    @DisplayName("Debe lanzar BranchNameEmptyException en addBranch cuando el nombre es nulo")
    void addBranch_NullName_Error() {
        StepVerifier.create(branchCase.addBranch(1, null))
                .expectError(BranchNameEmptyException.class)
                .verify();

        verify(franchisePersistencePort, never()).franchiseExistsById(any());
        verify(branchPersistencePort, never()).addBranch(any(), any());
    }

    @Test
    @DisplayName("Debe lanzar BranchNameEmptyException en addBranch cuando el nombre está en blanco")
    void addBranch_BlankName_Error() {
        StepVerifier.create(branchCase.addBranch(1, "   "))
                .expectError(BranchNameEmptyException.class)
                .verify();

        verify(franchisePersistencePort, never()).franchiseExistsById(any());
        verify(branchPersistencePort, never()).addBranch(any(), any());
    }
}