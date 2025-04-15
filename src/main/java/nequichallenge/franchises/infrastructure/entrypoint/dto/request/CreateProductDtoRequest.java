package nequichallenge.franchises.infrastructure.entrypoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDtoRequest {
    private String name;
    private Integer stock;
    private Integer branchId;
}
