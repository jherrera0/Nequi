package nequichallenge.franchises.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class ProductEntity {
    @Id
    private Integer id;

    private String name;
    private int stock;

    @Column("isActive")
    private Boolean isActive;

    @Column("branch_id")
    private Integer branchId;
}