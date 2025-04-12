package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class ProductNameEmptyException extends RuntimeException {
    public ProductNameEmptyException() {
        super(ConstExceptions.PRODUCT_NAME_EMPTY);
    }
}
