/*-
 * ---license-start
 * WorldHealthOrganization / tng-key-distribution
 * ---
 * Copyright (C) 2021 - 2024 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

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
