package nequichallenge.franchises.domain.usecase;

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

import static org.mockito.Mockito.when;

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

        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void createFranchise_shouldReturnEmptyMono_whenFranchisePersistencePortReturnsEmpty() {
        Franchise franchise = new Franchise();
        when(franchisePersistencePort.createFranchise(franchise)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseCase.createFranchise(franchise))
                .verifyComplete();
    }
}