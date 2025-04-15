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
@Table("branch")
public class BranchEntity {
    @Id
    private Integer id;
    private String name;

    @Column("franchise_id")
    private Integer franchiseId;
}
