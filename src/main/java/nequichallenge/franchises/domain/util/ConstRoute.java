package nequichallenge.franchises.domain.util;

public class ConstRoute {
    public static final String BRANCH_REST_ROUTE = "/branch";
    public static final String PRODUCT = "/product";
    public static final String FRANCHISE = "/franchise";
    public static final String CREATE = "/create";
    public static final String DELETE = "/delete";
    public static final String ADD_BRANCH_REST_ROUTE = "/addBranch";
    public static final String ADD_PRODUCT_STOCK_REST_ROUTE = "/addProductStock";
    public static final String GET_TOP_STOCK_PRODUCTS_BY_BRANCH_ASSOCIATED_TO_FRANCHISE
            = "/getTopStockProductsByBranchAssociatedToFranchise/{franchiseId}";
    public static final String UPDATE_NAME = "/updateName";
    public static final String HEALTH_CHECK = "/health";


    private ConstRoute() {}
}

