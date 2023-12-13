package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DidDocumentUnmarshal {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<String> context;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String id;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String controller;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private VerificationMethod verificationMethod;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Proof proof;

}
