package nequichallenge.franchises.application.http.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNameDtoRequest {
    private Integer id;
    private String name;
}
