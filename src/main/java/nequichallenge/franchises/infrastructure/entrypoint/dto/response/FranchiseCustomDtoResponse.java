package nequichallenge.franchises.infrastructure.entrypoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseCustomDtoResponse {
    private Integer id;
    private String name;
}
