package nequichallenge.franchises.infrastructure.entrypoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductStockDtoRequest {
    private Integer id;
    private Integer stock;
}
