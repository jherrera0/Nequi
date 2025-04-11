package nequichallenge.franchises.application.http.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDtoResponse {
    private Integer id;
    private String name;
    private List<ProductDtoResponse> products;
}
