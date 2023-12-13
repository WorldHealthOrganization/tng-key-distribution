package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Proof {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String type;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String created;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String nonce;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String proofPurpose;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String verificationMethod;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String jws;

}
