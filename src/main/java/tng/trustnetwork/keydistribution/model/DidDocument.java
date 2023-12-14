package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DidDocument {

    @JsonProperty("@context")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<String> context;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String id;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String controller;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Object verificationMethod;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Proof proof;

}
