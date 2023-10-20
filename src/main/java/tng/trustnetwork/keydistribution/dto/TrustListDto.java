package tng.trustnetwork.keydistribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrustListDto {
    private long id;

    private String country;

    private String name;
}
