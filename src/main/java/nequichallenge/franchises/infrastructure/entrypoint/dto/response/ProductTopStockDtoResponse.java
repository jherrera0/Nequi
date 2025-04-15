package nequichallenge.franchises.infrastructure.entrypoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTopStockDtoResponse {
    private Integer branchId;
    private String branchName;
    private Integer productId;
    private String productName;
    private int stock;
}
