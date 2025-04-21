package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException() {
        super(ConstExceptions.FRANCHISE_NOT_FOUND);
    }
}
