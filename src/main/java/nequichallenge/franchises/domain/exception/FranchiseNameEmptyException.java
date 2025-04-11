package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class FranchiseNameEmptyException extends RuntimeException {
    public FranchiseNameEmptyException() {
        super(ConstExceptions.FRANCHISE_NAME_EMPTY);
    }
}
