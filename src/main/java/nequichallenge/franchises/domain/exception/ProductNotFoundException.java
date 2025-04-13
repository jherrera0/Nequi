package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super(ConstExceptions.PRODUCT_NOT_FOUND);
    }
}
