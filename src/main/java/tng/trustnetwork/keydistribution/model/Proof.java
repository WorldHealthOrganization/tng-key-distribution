package tng.trustnetwork.keydistribution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Proof {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String type;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String created;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String nonce;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String proofPurpose;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String verificationMethod;
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String jws;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public String getProofPurpose() {
		return proofPurpose;
	}
	public void setProofPurpose(String proofPurpose) {
		this.proofPurpose = proofPurpose;
	}
	public String getVerificationMethod() {
		return verificationMethod;
	}
	public void setVerificationMethod(String verificationMethod) {
		this.verificationMethod = verificationMethod;
	}
	public String getJws() {
		return jws;
	}
	public void setJws(String jws) {
		this.jws = jws;
	}
	
}
