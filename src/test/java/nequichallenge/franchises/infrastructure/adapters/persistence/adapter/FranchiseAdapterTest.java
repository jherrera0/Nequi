package nequichallenge.franchises.infrastructure.adapters.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.FranchiseEntity;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IFranchiseEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IFranchiseRepository;
import nequichallenge.franchises.domain.model.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

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

        assertEquals(Boolean.TRUE, result.block());
        verify(franchiseRepository).existsByName(franchiseName);
    }

    @Test
    @DisplayName("should return false when franchise does not exist by name")
    void shouldReturnFalseWhenFranchiseDoesNotExistByName() {
        String franchiseName = "Non-Existing Franchise";

        when(franchiseRepository.existsByName(franchiseName)).thenReturn(Mono.just(false));

        Mono<Boolean> result = franchiseAdapter.franchiseExistsByName(franchiseName);

        assertNotEquals(Boolean.TRUE, result.block());
        verify(franchiseRepository).existsByName(franchiseName);
    }
    @Test
    @DisplayName("should return true when franchise exists by id")
    void shouldReturnTrueWhenFranchiseExistsById() {
        Integer franchiseId = 1;

        when(franchiseRepository.existsById(franchiseId)).thenReturn(Mono.just(true));

        Mono<Boolean> result = franchiseAdapter.franchiseExistsById(franchiseId);

        assertEquals(Boolean.TRUE, result.block());
        verify(franchiseRepository).existsById(franchiseId);
    }

    @Test
    @DisplayName("should return false when franchise does not exist by id")
    void shouldReturnFalseWhenFranchiseDoesNotExistById() {
        Integer franchiseId = 999;

        when(franchiseRepository.existsById(franchiseId)).thenReturn(Mono.just(false));

        Mono<Boolean> result = franchiseAdapter.franchiseExistsById(franchiseId);

        assertNotEquals(Boolean.TRUE, result.block());
        verify(franchiseRepository).existsById(franchiseId);
    }
    @Test
    @DisplayName("should return franchise when found by id")
    void shouldReturnFranchiseWhenFoundById() {
        Integer franchiseId = 1;
        FranchiseEntity franchiseEntity = new FranchiseEntity(franchiseId, "Franchise Name");
        Franchise franchise = new Franchise(franchiseId, "Franchise Name", List.of());

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchiseEntity));
        when(franchiseEntityMapper.toFranchise(franchiseEntity)).thenReturn(franchise);

        Mono<Franchise> result = franchiseAdapter.findById(franchiseId);

        assertEquals(franchise, result.block());
        verify(franchiseRepository).findById(franchiseId);
        verify(franchiseEntityMapper).toFranchise(franchiseEntity);
    }

    @Test
    @DisplayName("should return empty when franchise not found by id")
    void shouldReturnEmptyWhenFranchiseNotFoundById() {
        Integer franchiseId = 999;

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        Mono<Franchise> result = franchiseAdapter.findById(franchiseId);

        assertNull(result.block());
        verify(franchiseRepository).findById(franchiseId);
        verify(franchiseEntityMapper, never()).toFranchise(any());
    }

    @Test
    @DisplayName("should update franchise successfully when franchise exists")
    void shouldUpdateFranchiseSuccessfullyWhenFranchiseExists() {
        FranchiseEntity existingEntity = new FranchiseEntity(1, "Old Name");
        FranchiseEntity updatedEntity = new FranchiseEntity(1, "New Name");
        Franchise franchiseToUpdate = new Franchise(1, "New Name", List.of());
        Franchise updatedFranchise = new Franchise(1, "New Name", List.of());

        when(franchiseRepository.findById(1)).thenReturn(Mono.just(existingEntity));
        when(franchiseRepository.save(existingEntity)).thenReturn(Mono.just(updatedEntity));
        when(franchiseEntityMapper.toFranchise(updatedEntity)).thenReturn(updatedFranchise);

        Mono<Franchise> result = franchiseAdapter.updateFranchise(franchiseToUpdate);

        assertEquals(updatedFranchise, result.block());
        verify(franchiseRepository).findById(1);
        verify(franchiseRepository).save(existingEntity);
        verify(franchiseEntityMapper).toFranchise(updatedEntity);
    }
}