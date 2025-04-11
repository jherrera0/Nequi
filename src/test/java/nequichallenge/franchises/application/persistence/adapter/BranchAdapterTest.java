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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
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
}