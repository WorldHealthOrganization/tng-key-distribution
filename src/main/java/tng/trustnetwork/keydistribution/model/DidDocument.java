package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DidDocument {

    @JsonProperty("@context")
    private List<StringOrObject<DidContext>> context;

    private String id;

    private String controller;


    private List<StringOrObject<VerificationMethod>> verificationMethod;

    private Proof proof;

}
