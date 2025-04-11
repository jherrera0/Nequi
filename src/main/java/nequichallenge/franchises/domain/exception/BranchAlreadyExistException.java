package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class BranchAlreadyExistException extends RuntimeException{
    public BranchAlreadyExistException() {
        super(ConstExceptions.BRANCH_ALREADY_EXIST);
    }

}
