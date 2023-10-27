package tng.trustnetwork.keydistribution.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class DIDDocumentUnmarshal {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private List<String> context;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String id;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String controller;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private VerificationMethod verificationMethod;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private Proof proof;
	public List<String> getContext() {
		return context;
	}
	public void setContext(List<String> context) {
		this.context = context;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
	}
	public VerificationMethod getVerificationMethod() {
		return verificationMethod;
	}
	public void setVerificationMethod(VerificationMethod verificationMethod) {
		this.verificationMethod = verificationMethod;
	}
	public Proof getProof() {
		return proof;
	}
	public void setProof(Proof proof) {
		this.proof = proof;
	}
	
	

}
