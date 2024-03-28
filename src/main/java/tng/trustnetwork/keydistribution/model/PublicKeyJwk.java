package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(property = "kty", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(name = "EC", value = EcPublicKeyJwk.class),
    @JsonSubTypes.Type(name = "RSA", value = RsaPublicKeyJwk.class),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PublicKeyJwk {

    private String kty;

    private String kid;

    private String x5u;

    private List<String> x5c;

    private String x5t;

    @JsonProperty("x5t#S256")
    private String x5tS256;

    private Use use;

    @JsonProperty("key_ops")
    private List<KeyOps> keyOps;

    public enum KeyOps {
        @JsonProperty("sign")
        SIGN,

        @JsonProperty("verify")
        VERIFY,

        @JsonProperty("encrypt")
        ENCRYPT,

        @JsonProperty("decrypt")
        DECRYPT,

        @JsonProperty("wrapKey")
        WRAP_KEY,

        @JsonProperty("unwrapKey")
        UNWRAP_KEY,

        @JsonProperty("deriveKey")
        DERIVE_KEY,

        @JsonProperty("deriveBits")
        DERIVE_BITS

    }

    public enum Use {
        @JsonProperty("sig")
        SIGNATURE,

        @JsonProperty("enc")
        ENCRYPTION
    }

}
