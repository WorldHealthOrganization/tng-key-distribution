package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;

//@RequiredArgsConstructor
public class StringOrObjectDeserializer extends JsonDeserializer<StringOrObject<?>> implements ContextualDeserializer {

    private JavaType type;

    //private final ObjectMapper objectMapper;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {

        this.type = property.getType().containedType(0).containedType(0);
        return this;
    }

    @Override
    public StringOrObject<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException {

        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        if (jsonNode instanceof TextNode textNode) {

            return new StringOrObject<>(null, textNode.textValue());
        } else if (jsonNode instanceof ObjectNode objectNode) {

            return new StringOrObject<>(deserializationContext.readTreeAsValue(objectNode, type), null);
        } else {
            return null;
        }
    }
}
