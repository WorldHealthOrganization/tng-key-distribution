package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Proof {

    private String type;

    private String created;

    private String nonce;

    private String proofPurpose;

    private String verificationMethod;

    private String jws;

}
