package nequichallenge.franchises.infrastructure.entrypoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBranchDtoRequest {
    private Integer franchiseId;
    private String name;
}
