package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VerificationMethodDeserializer extends StdDeserializer<VerificationMethod> {

    public VerificationMethodDeserializer() {

        this(null);
    }

    protected VerificationMethodDeserializer(Class<?> vc) {

        super(vc);
    }

    @Override
    public VerificationMethod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        JsonNode node = p.getCodec().readTree(p);

        ObjectMapper mapper = new ObjectMapper();

        VerificationMethod verificationMethod = new VerificationMethod();
        List<ResolvedKey> resolvedKeys = new ArrayList<>();
        List<String> unResolvedKeys = new ArrayList<>();

        if (node.isArray()) {
            for (int i = 0; i <= node.size() - 1; i++) {
                JsonNode chileNode = node.get(i);
                Iterator<Map.Entry<String, JsonNode>> fields = chileNode.fields();

                if (fields.hasNext()) {
                    ResolvedKey resolvedKey = mapper.readValue(chileNode.toString(), ResolvedKey.class);
                    resolvedKeys.add(resolvedKey);
                } else {
                    unResolvedKeys.add(chileNode.toString());
                }
            }
            verificationMethod.setResolvedKeys(resolvedKeys);
            verificationMethod.setUnResolvedKeys(unResolvedKeys);
        }
        return verificationMethod;
    }

}
