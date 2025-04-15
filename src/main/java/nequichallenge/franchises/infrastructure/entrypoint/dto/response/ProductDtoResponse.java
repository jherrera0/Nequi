package nequichallenge.franchises.infrastructure.entrypoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDtoResponse {
    private Integer id;
    private String name;
    private Integer stock;
    private Boolean isActive;
}
