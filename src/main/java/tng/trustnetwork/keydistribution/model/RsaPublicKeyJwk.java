package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsaPublicKeyJwk extends PublicKeyJwk {

    @JsonProperty("n")
    private String nvalue;

    @JsonProperty("e")
    private String evalue;

}
