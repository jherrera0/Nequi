package nequichallenge.franchises.application.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import nequichallenge.franchises.application.persistence.entity.FranchiseEntity;
import nequichallenge.franchises.application.persistence.mapper.IFranchiseEntityMapper;
import nequichallenge.franchises.application.persistence.repository.IFranchiseRepository;
import nequichallenge.franchises.domain.model.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class FranchiseAdapterTest {

    private FranchiseAdapter franchiseAdapter;
    private IFranchiseEntityMapper franchiseEntityMapper;
    private IFranchiseRepository franchiseRepository;

    @BeforeEach
    void setUp() {
        franchiseEntityMapper = mock(IFranchiseEntityMapper.class);
        franchiseRepository = mock(IFranchiseRepository.class);
        franchiseAdapter = new FranchiseAdapter(franchiseEntityMapper, franchiseRepository);
    }

    @Nested
    @DisplayName("createFranchise")
    class CreateFranchise {

        @Test
        @DisplayName("should create a franchise successfully")
        void shouldCreateFranchiseSuccessfully() {
            Franchise franchise = new Franchise();
            FranchiseEntity franchiseEntity = new FranchiseEntity();
            Franchise savedFranchise = new Franchise();

            when(franchiseEntityMapper.toFranchiseEntity(franchise)).thenReturn(franchiseEntity);
            when(franchiseRepository.save(franchiseEntity)).thenReturn(Mono.just(franchiseEntity));
            when(franchiseEntityMapper.toFranchise(franchiseEntity)).thenReturn(savedFranchise);

            Mono<Franchise> result = franchiseAdapter.createFranchise(franchise);

            assertEquals(savedFranchise, result.block());
            verify(franchiseEntityMapper).toFranchiseEntity(franchise);
            verify(franchiseRepository).save(franchiseEntity);
            verify(franchiseEntityMapper).toFranchise(franchiseEntity);
        }
    }
    @Test
    @DisplayName("should return true when franchise exists by name")
    void shouldReturnTrueWhenFranchiseExistsByName() {
        String franchiseName = "Existing Franchise";

        when(franchiseRepository.existsByName(franchiseName)).thenReturn(Mono.just(true));

        Mono<Boolean> result = franchiseAdapter.franchiseExistsByName(franchiseName);

        assertTrue(result.block());
        verify(franchiseRepository).existsByName(franchiseName);
    }

    @Test
    @DisplayName("should return false when franchise does not exist by name")
    void shouldReturnFalseWhenFranchiseDoesNotExistByName() {
        String franchiseName = "Non-Existing Franchise";

        when(franchiseRepository.existsByName(franchiseName)).thenReturn(Mono.just(false));

        Mono<Boolean> result = franchiseAdapter.franchiseExistsByName(franchiseName);

        assertFalse(result.block());
        verify(franchiseRepository).existsByName(franchiseName);
    }
    @Test
    @DisplayName("should return true when franchise exists by id")
    void shouldReturnTrueWhenFranchiseExistsById() {
        Integer franchiseId = 1;

        when(franchiseRepository.existsById(franchiseId)).thenReturn(Mono.just(true));

        Mono<Boolean> result = franchiseAdapter.franchiseExistsById(franchiseId);

        assertTrue(result.block());
        verify(franchiseRepository).existsById(franchiseId);
    }

    @Test
    @DisplayName("should return false when franchise does not exist by id")
    void shouldReturnFalseWhenFranchiseDoesNotExistById() {
        Integer franchiseId = 999;

        when(franchiseRepository.existsById(franchiseId)).thenReturn(Mono.just(false));

        Mono<Boolean> result = franchiseAdapter.franchiseExistsById(franchiseId);

        assertFalse(result.block());
        verify(franchiseRepository).existsById(franchiseId);
    }
}