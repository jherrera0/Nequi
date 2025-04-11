package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class BranchNameEmptyException extends RuntimeException {
    public BranchNameEmptyException() {
        super(ConstExceptions.BRANCH_NAME_EMPTY);
    }
}
