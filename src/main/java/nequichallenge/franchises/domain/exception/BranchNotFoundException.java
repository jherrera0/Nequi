package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException() {
        super(ConstExceptions.BRANCH_NOT_FOUND);
    }
}
