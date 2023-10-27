package tng.trustnetwork.keydistribution.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class VMDeserializer extends StdDeserializer<VerificationMethod> {

	protected VMDeserializer(Class<?> vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}
	
	public VMDeserializer() { 
        this(null); 
    } 
	
	private static final long serialVersionUID = 1L;

	@Override
	public VerificationMethod deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JacksonException {

		 JsonNode node = p.getCodec().readTree(p);
		 
		 ObjectMapper mapper = new ObjectMapper();
		 
		 VerificationMethod verificationMethod = new VerificationMethod();
		 List<ResolvedKey> resolvedKeys = new ArrayList<ResolvedKey>();
		 List<String> unResolvedKeys = new ArrayList<String>();
		 
		 if(node.isArray()) {
			 for(int i= 0; i<=node.size()-1 ; i++) {
				 JsonNode chileNode = node.get(i);
				 Iterator<Map.Entry<String, JsonNode>> fields = chileNode.fields();
				 
				 if(fields.hasNext()) {
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
