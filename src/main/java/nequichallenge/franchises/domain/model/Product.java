package nequichallenge.franchises.domain.model;

public class Product {
    private Integer id;
    private String name;
    private Integer stock;
    private Boolean isActive;

    public Product() {

    }

    public Product(Integer id, String name, Integer stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Product withActiveStatus(Boolean active) {
        Product copy = new Product(this.id, this.name, this.stock);
        copy.isActive = active;
        return copy;
    }

    public Product withStock(Integer newStock) {
        Product copy = new Product(this.id, this.name, this.stock+newStock);
        copy.isActive = this.isActive;
        return copy;
    }

    public Product withName(String newName) {
        Product copy = new Product(this.id, newName, this.stock);
        copy.isActive = this.isActive;
        return copy;
    }

    public ProductTopStock toTopStock(Branch branch) {
        return new ProductTopStock(branch.getId(), branch.getName(), this.id, this.name, this.stock);
    }
}
