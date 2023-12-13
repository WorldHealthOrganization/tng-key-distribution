package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicKeyJwk {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String kty;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String crv;
    @JsonProperty("x")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String xvalue;
    @JsonProperty("y")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String yvalue;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<String> x5c;

}
