package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonDeserialize(using = VerificationMethodDeserializer.class)
public class VerificationMethod {

    private List<ResolvedKey> resolvedKeys;
    private List<String> unResolvedKeys;

    public VerificationMethod() {

    }
}
