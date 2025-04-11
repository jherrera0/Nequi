package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class FranchiseAlreadyExistsException extends RuntimeException {
    public FranchiseAlreadyExistsException() {
        super(ConstExceptions.FRANCHISE_ALREADY_EXISTS);
    }
}
