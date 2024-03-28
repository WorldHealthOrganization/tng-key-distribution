package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = JwkVerificationMethod.class, name = "JsonWebKey2020")
})
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class VerificationMethod {

    private String id;

    private String controller;

    private String type;

}
