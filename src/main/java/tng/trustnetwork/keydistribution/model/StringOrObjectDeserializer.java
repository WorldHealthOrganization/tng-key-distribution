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
