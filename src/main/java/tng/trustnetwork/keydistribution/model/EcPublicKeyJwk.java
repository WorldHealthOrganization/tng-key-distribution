package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EcPublicKeyJwk extends PublicKeyJwk {

    private Curve crv;

    @JsonProperty("x")
    private String xvalue;

    @JsonProperty("y")
    private String yvalue;

    public enum Curve {
        @JsonEnumDefaultValue
        UNKNOWN,

        @JsonProperty("P-256")
        P256,

        @JsonProperty("P-384")
        P384,

        @JsonProperty("P-521")
        P521

    }

}
