package nequichallenge.franchises.application.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    private Integer id;
    private String name;
    private int stock;

    @Column("branch_id")
    private Integer branchId;
}