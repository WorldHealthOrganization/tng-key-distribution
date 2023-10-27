package tng.trustnetwork.keydistribution.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize(using = VMDeserializer.class)
public class VerificationMethod {
	
	private List<ResolvedKey> resolvedKeys;
	private List<String> unResolvedKeys;
	
	
	public VerificationMethod(String nextString, boolean b) {
	}
	public VerificationMethod() {
	}
	public List<ResolvedKey> getResolvedKeys() {
		return resolvedKeys;
	}
	public void setResolvedKeys(List<ResolvedKey> resolvedKeys) {
		this.resolvedKeys = resolvedKeys;
	}
	public List<String> getUnResolvedKeys() {
		return unResolvedKeys;
	}
	public void setUnResolvedKeys(List<String> unResolvedKeys) {
		this.unResolvedKeys = unResolvedKeys;
	}

}
