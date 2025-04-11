package nequichallenge.franchises.domain.model;

import nequichallenge.franchises.domain.exception.FranchiseNameEmptyException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {
    @Test
    void shouldCreateFranchiseWithValidData() {
        Franchise franchise = new Franchise(1, "Franchise Name", List.of(new Branch(1, "Branch1", null)));
        assertEquals(1, franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertEquals(1, franchise.getBranches().size());
    }

    @Test
    void shouldCreateFranchiseWithEmptyBranchList() {
        Franchise franchise = new Franchise();
        franchise.setId(1);
        franchise.setName("Franchise Name");
        franchise.setBranches(List.of());
        assertEquals(1, franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void shouldAllowUpdatingFranchiseName() {
        Franchise franchise = new Franchise();
        franchise.setName("Updated Franchise Name");
        assertEquals("Updated Franchise Name", franchise.getName());
    }

    @Test
    void shouldHandleEmptyBranchList() {
        Franchise franchise = new Franchise(1, "Franchise Name", List.of());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void shouldAllowAddingBranchesToFranchise() {
        Franchise franchise = new Franchise();
        franchise.setBranches(List.of(new Branch(1, "Branch1", null)));
        assertEquals(1, franchise.getBranches().size());
        assertEquals("Branch1", franchise.getBranches().get(0).getName());
    }

    @Test
    void shouldHandleNullBranchesGracefully() {
        Franchise franchise = new Franchise(1, "Franchise Name", null);
        assertNull(franchise.getBranches());
    }
    @Test
    void throwsExceptionWhenFranchiseNameIsNull() {
        FranchiseNameEmptyException exception = assertThrows(FranchiseNameEmptyException.class,
                this::createFranchiseWithName);
        assertNotNull(exception);
    }

    @Test
    void allowsSettingValidFranchiseName() {
        Franchise franchise = new Franchise();
        franchise.setName("Valid Name");
        assertEquals("Valid Name", franchise.getName());
    }

    @Test
    void throwsExceptionWhenSettingNullFranchiseName() {
        Franchise franchise = new Franchise();
        FranchiseNameEmptyException exception = assertThrows(FranchiseNameEmptyException.class, () -> {
            franchise.setName(null);
        });
        assertNotNull(exception);
    }

    @Test
    void throwsExceptionWhenSettingEmptyFranchiseName() {
        Franchise franchise = new Franchise();
        FranchiseNameEmptyException exception = assertThrows(FranchiseNameEmptyException.class, () -> {
            franchise.setName("  ");
        });
        assertNotNull(exception);
    }
    private void createFranchiseWithName() {
        new Franchise(1, null, List.of());
    }
}