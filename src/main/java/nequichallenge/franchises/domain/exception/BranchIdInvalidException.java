package nequichallenge.franchises.domain.exception;


import nequichallenge.franchises.domain.util.ConstExceptions;

public class BranchIdInvalidException extends RuntimeException {
    public BranchIdInvalidException() {
        super(ConstExceptions.BRANCH_ID_INVALID);
    }
}
