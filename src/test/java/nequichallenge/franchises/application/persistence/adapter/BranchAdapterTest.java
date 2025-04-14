package nequichallenge.franchises.application.persistence.adapter;

import nequichallenge.franchises.application.persistence.entity.BranchEntity;
import nequichallenge.franchises.application.persistence.mapper.IBranchEntityMapper;
import nequichallenge.franchises.application.persistence.repository.IBranchRepository;
import nequichallenge.franchises.domain.model.Branch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchAdapterTest {

    @Mock
    private IBranchRepository branchRepository;

    @Mock
    private IBranchEntityMapper branchEntityMapper;

    @InjectMocks
    private BranchAdapter branchAdapter;

    @Test
    void addBranchShouldReturnBranchWhenSaveIsSuccessful() {
        Integer franchiseId = 1;
        String name = "Test Branch";
        BranchEntity entity = new BranchEntity();
        entity.setName(name);
        entity.setFranchiseId(franchiseId);

        BranchEntity savedEntity = new BranchEntity();
        savedEntity.setId(1);
        savedEntity.setName(name);
        savedEntity.setFranchiseId(franchiseId);

        Branch branch = new Branch();
        branch.setId(1);
        branch.setName(name);

        when(branchRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(branchEntityMapper.toModel(savedEntity)).thenReturn(branch);

        StepVerifier.create(branchAdapter.addBranch(franchiseId, name))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void addBranchShouldReturnErrorWhenSaveFails() {
        Integer franchiseId = 1;
        String name = "Test Branch";
        BranchEntity entity = new BranchEntity();
        entity.setName(name);
        entity.setFranchiseId(franchiseId);

        when(branchRepository.save(entity)).thenReturn(Mono.error(new RuntimeException("Save failed")));

        StepVerifier.create(branchAdapter.addBranch(franchiseId, name))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Save failed"))
                .verify();
    }

    @Test
    void existsByNameShouldReturnTrueWhenBranchExists() {
        String name = "Existing Branch";

        when(branchRepository.existsByName(name)).thenReturn(Mono.just(true));

        StepVerifier.create(branchAdapter.existsByName(name))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByNameShouldReturnFalseWhenBranchDoesNotExist() {
        String name = "Nonexistent Branch";

        when(branchRepository.existsByName(name)).thenReturn(Mono.just(false));

        StepVerifier.create(branchAdapter.existsByName(name))
                .expectNext(false)
                .verifyComplete();
    }
    @Test
    void existsByIdShouldReturnTrueWhenBranchExists() {
        Integer branchId = 1;

        when(branchRepository.existsById(branchId)).thenReturn(Mono.just(true));

        StepVerifier.create(branchAdapter.existsById(branchId))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByIdShouldReturnFalseWhenBranchDoesNotExist() {
        Integer branchId = 1;

        when(branchRepository.existsById(branchId)).thenReturn(Mono.just(false));

        StepVerifier.create(branchAdapter.existsById(branchId))
                .expectNext(false)
                .verifyComplete();
    }
    @Test
    void getBranchesByFranchiseIdShouldReturnBranchesWhenFranchiseIdIsValid() {
        Integer franchiseId = 1;
        List<BranchEntity> entities = List.of(
                new BranchEntity(1, "Branch 1", franchiseId),
                new BranchEntity(2, "Branch 2", franchiseId)
        );
        List<Branch> branches = List.of(
                new Branch(1, "Branch 1",List.of()),
                new Branch(2, "Branch 2",List.of())
        );

        when(branchRepository.findAllByFranchiseId(franchiseId)).thenReturn(Flux.fromIterable(entities));
        when(branchEntityMapper.toModel(entities.get(0))).thenReturn(branches.get(0));
        when(branchEntityMapper.toModel(entities.get(1))).thenReturn(branches.get(1));

        StepVerifier.create(branchAdapter.getBranchesByFranchiseId(franchiseId))
                .expectNext(branches.get(0))
                .expectNext(branches.get(1))
                .verifyComplete();
    }

    @Test
    void getBranchesByFranchiseIdShouldReturnEmptyWhenNoBranchesExist() {
        Integer franchiseId = 1;

        when(branchRepository.findAllByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        StepVerifier.create(branchAdapter.getBranchesByFranchiseId(franchiseId))
                .verifyComplete();
    }
}