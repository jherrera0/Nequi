package nequichallenge.franchises.domain.exception;

import nequichallenge.franchises.domain.util.ConstExceptions;

public class ProductStockInvalidException extends RuntimeException {
    public ProductStockInvalidException() {
        super(ConstExceptions.PRODUCT_STOCK_INVALID);
    }

}
