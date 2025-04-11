package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
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
}