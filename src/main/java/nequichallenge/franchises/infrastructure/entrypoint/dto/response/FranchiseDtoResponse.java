package nequichallenge.franchises.infrastructure.entrypoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseDtoResponse {
    private Integer id;
    private String name;
    private List<BranchDtoResponse> branches;
}
