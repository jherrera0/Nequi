package nequichallenge.franchises.domain.model;

public class ProductTopStock {
    private Integer branchId;
    private String branchName;
    private Integer productId;
    private String productName;
    private int stock;

    public ProductTopStock() {
    }

    public ProductTopStock(Integer branchId, String branchName, Integer productId, String productName, int stock) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.productId = productId;
        this.productName = productName;
        this.stock = stock;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
