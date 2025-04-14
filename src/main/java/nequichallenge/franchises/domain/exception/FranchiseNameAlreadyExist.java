package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class FranchiseNameAlreadyExist extends RuntimeException {
    public FranchiseNameAlreadyExist() {
        super(ConstExceptions.FRANCHISE_NAME_ALREADY_EXIST);
    }

}
